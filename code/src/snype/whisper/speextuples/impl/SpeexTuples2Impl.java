package snype.whisper.speextuples.impl;

import static basis.environments.Environments.my;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sound.sampled.LineUnavailableException;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.streams.sequencer.Sequencer;
import sneer.bricks.pulp.streams.sequencer.Sequencers;
import snype.whisper.skin.audio.mic.Mic;
import snype.whisper.skin.audio.speaker.Speaker;
import snype.whisper.skin.audio.speaker.Speaker.Line;
import snype.whisper.speex.Decoder;
import snype.whisper.speex.Encoder;
import snype.whisper.speex.Speex;
import snype.whisper.speextuples.SpeexPacket;
import snype.whisper.speextuples.SpeexTuples2;
import basis.lang.ByRef;
import basis.lang.CacheMap;
import basis.lang.Consumer;
import basis.lang.Producer;
import basis.lang.arrays.ImmutableByteArray;
import basis.lang.arrays.ImmutableByteArray2D;

class SpeexTuples2Impl implements SpeexTuples2 { //Refactor Break this into the encoding and decoding sides.

	private final Speex _speex = my(Speex.class);

	private final Set<Seal> _talkingTo = Collections.synchronizedSet(new HashSet<Seal>());
	private byte[][] _frames = newFramesArray();
	private int _frameIndex;
	
	private final Encoder _encoder = _speex.createEncoder();
	private final Decoder _decoder = _speex.createDecoder();
	
	private final AtomicInteger _ids = new AtomicInteger();
	@SuppressWarnings("unused")	private WeakContract _micSoundContract;
	
	private final CacheMap<Seal, Sequencer<SpeexPacket>> _sequencersByPublisher = CacheMap.newInstance();
	private Producer<Sequencer<SpeexPacket>> _sequencerProducer = sequencerProducer();
	@SuppressWarnings("unused")	private final WeakContract _tupleSpaceContract;
	
	public SpeexTuples2Impl() {

		_micSoundContract = my(Mic.class).sound().addReceiver(new Consumer<ImmutableByteArray>() { @Override public void consume(ImmutableByteArray packet) {
			if (encode(packet.copy()))
				flush();
		}});

		_tupleSpaceContract = my(RemoteTuples.class).addSubscription(SpeexPacket.class, new Consumer<SpeexPacket>() { @Override public void consume(SpeexPacket packet) {
			if (!_talkingTo.contains(packet.publisher)) return;

			playInSequence(packet);
		}});
	}
	
	@Override
	public void addTalker(Seal who) {
		_talkingTo.add(who);
	}

	@Override
	public void removeTalker(Seal who) {
		_talkingTo.remove(who);
	}

	@Override
	public boolean hasTalkers() {
		return _talkingTo.size()!=0;
	}
	
	private void playInSequence(SpeexPacket packet) {
		Sequencer<SpeexPacket> sequencer = _sequencersByPublisher.get(packet.publisher, _sequencerProducer);
		sequencer.produceInSequence(packet, packet.sequence);
	}
	
	
	private Producer<Sequencer<SpeexPacket>> sequencerProducer() {
		return new Producer<Sequencer<SpeexPacket>>() { @Override public Sequencer<SpeexPacket> produce() {
			final ByRef<Line> speakerLine = ByRef.newInstance();
			return my(Sequencers.class).createSequencerFor((short)15, (short)150, new Consumer<SpeexPacket>(){ @Override public void consume(SpeexPacket sequencedPacket) {
				play(sequencedPacket, speakerLine);
			}});
		}};
	}

	private short nextShort() {
		if(_ids.compareAndSet(Short.MAX_VALUE, Short.MIN_VALUE))
			return Short.MIN_VALUE;
		return (short)_ids.incrementAndGet();
	}

	private static byte[][] newFramesArray() {
		return new byte[FRAMES_PER_AUDIO_PACKET][];
	}

	private void flush() {
		for(Seal seal : _talkingTo) {
			my(TupleSpace.class).add(new SpeexPacket(seal, immutable(_frames), "#private#", nextShort()));
		}
		_frames = newFramesArray();
		_frameIndex = 0;
	}

	private ImmutableByteArray2D immutable(byte[][] array2D) {
		return new ImmutableByteArray2D(array2D);
	}

	private boolean encode(final byte[] pcmBuffer) {
		if (!_encoder.processData(pcmBuffer)) return false;
		
		_frames[_frameIndex++] = _encoder.getProcessedData();
		return _frameIndex == FRAMES_PER_AUDIO_PACKET;
	}
	
	
	private void play(SpeexPacket packet, ByRef<Line> speakerLine) {
		if (!initSpeakerLine(speakerLine)) return;
		for (byte[] frame : _decoder.decode(packet.frames.copy()))
			speakerLine.value.consume(frame);
	}

	
	private boolean initSpeakerLine(ByRef<Line> speakerLine) {
		if (speakerLine.value != null) return true;
		try {
			speakerLine.value = my(Speaker.class).acquireLine();
		} catch (LineUnavailableException e) {
			return false;
		}
		return true;
	}

}