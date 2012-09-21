package snype.whisper.speextuples.tests;

import static basis.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.skin.rooms.ActiveRoomKeeper;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import snype.whisper.speex.Decoder;
import snype.whisper.speex.Encoder;
import snype.whisper.speex.Speex;
import snype.whisper.speextuples.SpeexPacket;
import snype.whisper.speextuples.SpeexTuples;
import basis.brickness.testsupport.Bind;
import basis.lang.ByRef;
import basis.lang.Consumer;
import basis.lang.arrays.ImmutableByteArray;
import basis.lang.arrays.ImmutableByteArray2D;
import basis.lang.exceptions.NotImplementedYet;


@Ignore //SpeexTuples no longer produces PcmSoundPacket tuples. It uses Mic.sound() and Speaker.acquireLine() directly. This test must be fixed accordingly.
public class SpeexTuplesTest extends BrickTestBase {

	private final TupleSpace _tupleSpace = my(TupleSpace.class);
	
	@Bind private final Speex _speex = mock(Speex.class);
	private final Encoder _encoder = mock(Encoder.class);
	private final Decoder _decoder = mock(Decoder.class);
	
	{
		checking(new Expectations() {{
			allowing(_speex).createEncoder(); will(returnValue(_encoder));
			allowing(_speex).createDecoder(); will(returnValue(_decoder));
		}});
	}
	
	@SuppressWarnings("unused")
	private final SpeexTuples _subject = my(SpeexTuples.class);
	
	@Test (timeout = 4000)
	public void testPcmToSpeex() throws Exception {
		
		checking(new Expectations() {{ 
			for (byte i=0; i<SpeexTuples.FRAMES_PER_AUDIO_PACKET * 2; i+=2) {
				one(_encoder).processData(new byte[] { i }); will(returnValue(false));
				one(_encoder).processData(new byte[] { (byte) (i + 1) });	will(returnValue(true));
				one(_encoder).getProcessedData();	will(returnValue(new byte[] { (byte) (i*42) }));
			}
		}});
		
		
		final ByRef<SpeexPacket> packet = ByRef.newInstance();
		@SuppressWarnings("unused")
		WeakContract contract = my(RemoteTuples.class).addSubscription(SpeexPacket.class, new Consumer<SpeexPacket>() { @Override public void consume(SpeexPacket value) {
			assertNull(packet.value);
			packet.value = value;
		}});
		
		setRoom("MyChannel");
		for (byte[] frame : frames())
			_tupleSpace.add(myPacket(frame));
		
		my(TupleDispatcher.class).waitForAllDispatchingToFinish();
		
		assertNotNull(packet.value);
		assertFrames(packet.value.frames.copy());
		assertEquals("MyChannel", packet.value.room);
	}
	
	
	@Test (timeout = 4000)
	public void testSpeexToPcm() {
		final byte[][] speexPacketPayload = new byte[][] { {0} };
		final byte[] pcmPacketPayload = new byte[] { 17 };
		
		checking(new Expectations() {{ 
			one(_decoder).decode(speexPacketPayload);
				will(returnValue(new byte[][] { pcmPacketPayload }));
		}});
		
		setRoom("MyRoom");
		
		final ByRef<PcmSoundPacket> packet = ByRef.newInstance();
		@SuppressWarnings("unused")
		WeakContract contract = my(RemoteTuples.class).addSubscription(PcmSoundPacket.class, new Consumer<PcmSoundPacket>() { @Override public void consume(PcmSoundPacket value) {
			assertNull(packet.value);
			packet.value = value;
		}});
		
		_tupleSpace.add(speexPacketFrom(contactKey(), speexPacketPayload, "MyRoom", (short)0));
		// tuples with ownPublicKey should be ignored
		_tupleSpace.add(speexPacketFrom(ownPublicKey(), speexPacketPayload, "MyRoom", (short)1));
			// tuples with different channel should be ignored
		_tupleSpace.add(speexPacketFrom(contactKey(), speexPacketPayload, "OtherRoom", (short)2));
		
		my(TupleDispatcher.class).waitForAllDispatchingToFinish();
		final PcmSoundPacket pcmPacket = packet.value;
		assertNotNull(pcmPacket);
		assertArrayEquals(pcmPacketPayload, pcmPacket.payload.copy());
		assertEquals(contactKey(), pcmPacket.publisher);
	}

	private void setRoom(String name) {
		my(ActiveRoomKeeper.class).setter().consume(name);
	}

	@SuppressWarnings("unused") 
	private Tuple speexPacketFrom(Seal contactKey, byte[][] bs, String channel, short sequence) {
		SpeexPacket result = new SpeexPacket(immutable(bs), channel, sequence);
		//result.stamp(contactKey, 0);
		//return result;
		throw new NotImplementedYet("Mock the Seals.ownSeal() method to generate tuples with different publishers.");
	}

	private ImmutableByteArray2D immutable(byte[][] array2D) {
		return new ImmutableByteArray2D(array2D);
	}

	private void assertFrames(final byte[][] frames) {
		assertEquals(SpeexTuples.FRAMES_PER_AUDIO_PACKET, frames.length);
		int i = 0;
		for (byte[] frame : frames)  {
			assertArrayEquals(new byte[] { (byte) (i*42) }, frame);
			i += 2;
		}
	}
	
	private Seal contactKey() {
		return new Seal(new ImmutableByteArray("anything".getBytes()));
	}

	private PcmSoundPacket myPacket(byte[] pcm) {
		return pcmSoundPacketFrom(ownPublicKey(), pcm);
	}

	private Seal ownPublicKey() {
		return my(OwnSeal.class).get().currentValue();
	}

	@SuppressWarnings("unused")
	private PcmSoundPacket pcmSoundPacketFrom(Seal publicKey, final byte[] pcmPayload) {
		PcmSoundPacket result = new PcmSoundPacket(new ImmutableByteArray(pcmPayload));
		//result.stamp(publicKey, _clock.time().currentValue());
		//return result;
		throw new NotImplementedYet("Mock the Seals.ownSeal() method to generate tuples with different publishers.");
	}
	
	private byte[][] frames() {
		byte[][] frames = new byte[SpeexTuples.FRAMES_PER_AUDIO_PACKET * 2][];
		for (int i=0; i<frames.length; ++i)
			frames[i] = new byte[] { (byte) i };
		return frames;
	}

}
