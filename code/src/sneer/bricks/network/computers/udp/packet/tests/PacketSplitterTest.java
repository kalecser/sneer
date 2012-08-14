package sneer.bricks.network.computers.udp.packet.tests;

import static basis.environments.Environments.my;
import static java.lang.String.format;

import java.nio.ByteBuffer;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.udp.packet.PacketSplitter;
import sneer.bricks.network.computers.udp.packet.PacketSplitters;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.util.concurrent.RefLatch;

public class PacketSplitterTest extends BrickTestBase {

	
	private final PacketSplitter subject = my(PacketSplitters.class).newInstance();

	@Test(timeout=1000)
	public void splitAndJoinPackets() {
		splitAndJoin("", 2);
		splitAndJoin("Hey Neide", 3);
		splitAndJoin("How are you!?", 6);
		splitAndJoin("Hey Neide", 10);
		splitAndJoin("How are you?", 15);
		
		// 255 pieces
		splitAndJoin("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh " +
					"euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
					"minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut " +
					"aliquip ex eax.", 1);
	}
	

	private void splitAndJoin(String packet, int pieceSize) {
		ByteBuffer[] pieces = assertSplit(packet, pieceSize);
		assertJoin(packet, pieces);
	}


	private ByteBuffer[] assertSplit(String expected, int maxPieceSize) {
		ByteBuffer[] ret = subject.split(ByteBuffer.wrap(expected.getBytes()), maxPieceSize);
		
		for (ByteBuffer piece : ret) {
			assertTrue(format("Packet \"%s\" should have less than %s bytes", new String(piece.array()), maxPieceSize), 
					piece.remaining() <= maxPieceSize);
		}
		
		return ret;
	}


	private void assertJoin(String expected, ByteBuffer[] pieces) {
		RefLatch<ByteBuffer> latch = new RefLatch<>();
		@SuppressWarnings("unused")	WeakContract refToAvoidGC = subject.lastJoinedPacket().addReceiver(latch);
		
		for (ByteBuffer piece : pieces) 
			subject.join(piece);
		
		ByteBuffer joinedPacket = latch.waitAndGet();
		byte[] bytes = new byte[joinedPacket.remaining()];
		joinedPacket.get(bytes);
		
		assertEquals(expected, new String(bytes));
	}
	
}
