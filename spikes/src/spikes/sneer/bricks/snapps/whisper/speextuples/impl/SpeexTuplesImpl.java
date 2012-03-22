package spikes.sneer.bricks.snapps.whisper.speextuples.impl;

import static basis.environments.Environments.my;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sound.sampled.LineUnavailableException;

import basis.lang.ByRef;
import basis.lang.CacheMap;
import basis.lang.Consumer;
import basis.lang.Producer;
import basis.lang.arrays.ImmutableByteArray;
import basis.lang.arrays.ImmutableByteArray2D;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.streams.sequencer.Sequencer;
import sneer.bricks.pulp.streams.sequencer.Sequencers;
import sneer.bricks.skin.rooms.ActiveRoomKeeper;
import spikes.sneer.bricks.skin.audio.mic.Mic;
import spikes.sneer.bricks.skin.audio.speaker.Speaker;
import spikes.sneer.bricks.skin.audio.speaker.Speaker.Line;
import spikes.sneer.bricks.snapps.whisper.speex.Decoder;
import spikes.sneer.bricks.snapps.whisper.speex.Encoder;
import spikes.sneer.bricks.snapps.whisper.speex.Speex;
import spikes.sneer.bricks.snapps.whisper.speextuples.SpeexPacket;
import spikes.sneer.bricks.snapps.whisper.speextuples.SpeexTuples;

class SpeexTuplesImpl implements SpeexTuples { //Refactor Break this into the encoding and decoding sides.

	private final Speex _speex = my(Speex.class);

	private final Signal<String> _room = my(ActiveRoomKeeper.class).room();
	private byte[][] _frames = newFramesArray();
	private int _frameIndex;
	
	private final Encoder _encoder = _speex.createEncoder();
	private final Decoder _decoder = _speex.createDecoder();
	
	private final AtomicInteger _ids = new AtomicInteger();
	@SuppressWarnings("unused")	private WeakContract _micSoundContract;
	
	private final CacheMap<Seal, Sequencer<SpeexPacket>> _sequencersByPublisher = CacheMap.newInstance();
	private Producer<Sequencer<SpeexPacket>> _sequencerProducer = sequencerProducer();
	@SuppressWarnings("unused")	private final WeakContract _tupleSpaceContract;
	
	public SpeexTuplesImpl() {

		_micSoundContract = my(Mic.class).sound().addReceiver(new Consumer<ImmutableByteArray>() { @Override public void consume(ImmutableByteArray packet) {
			if (encode(packet.copy()))
				flush();
		}});

		_tupleSpaceContract = my(RemoteTuples.class).addSubscription(SpeexPacket.class, new Consumer<SpeexPacket>() { @Override public void consume(SpeexPacket packet) {
			if (!_room.currentValue().equals(packet.room)) return;

			playInSequence(packet);
		}});
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
		my(TupleSpace.class).add(new SpeexPacket(immutable(_frames), _room.currentValue(), nextShort()));
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