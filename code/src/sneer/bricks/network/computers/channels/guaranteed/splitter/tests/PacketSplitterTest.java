package sneer.bricks.network.computers.channels.guaranteed.splitter.tests;

import static basis.environments.Environments.my;
import static java.lang.String.format;

import java.nio.ByteBuffer;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitter;
import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitters;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.util.concurrent.RefLatch;

public class PacketSplitterTest extends BrickTestBase {

	private final PacketSplitters subject = my(PacketSplitters.class);

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
	
	
	@Test(expected=IllegalArgumentException.class)
	public void packetTooBig() {
		splitAndJoin("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh " +
				"euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
				"minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut " +
				"aliquip ex eax..", 1);
	}
	

	private void splitAndJoin(String packet, int maxPieceSize) {
		PacketSplitter splitter = subject.newInstance(maxPieceSize);
		
		ByteBuffer[] pieces = assertSplit(splitter, packet, maxPieceSize);
		assertJoin(splitter, packet, pieces);
		
		for (ByteBuffer piece : pieces) piece.flip();
		assertJoin(splitter, packet, pieces);
	}


	private ByteBuffer[] assertSplit(PacketSplitter splitter, String expected, int maxPieceSize) {
		ByteBuffer[] ret = splitter.split(ByteBuffer.wrap(expected.getBytes()));
		
		for (ByteBuffer piece : ret) {
			assertTrue(format("Packet \"%s\" should have less than %s bytes", new String(piece.array()), maxPieceSize), 
					piece.remaining() <= maxPieceSize);
		}
		
		return ret;
	}


	private void assertJoin(PacketSplitter splitter, String expected, ByteBuffer[] pieces) {
		RefLatch<ByteBuffer> latch = new RefLatch<>();
		@SuppressWarnings("unused")	WeakContract refToAvoidGC = splitter.lastJoinedPacket().addReceiver(latch);
		
		for (ByteBuffer piece : pieces) 
			splitter.join(piece);
		
		ByteBuffer joinedPacket = latch.waitAndGet();
		byte[] bytes = new byte[joinedPacket.remaining()];
		joinedPacket.get(bytes);
		
		assertEquals(expected, new String(bytes));
	}
	
}
