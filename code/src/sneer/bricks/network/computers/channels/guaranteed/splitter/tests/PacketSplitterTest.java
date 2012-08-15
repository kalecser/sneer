package sneer.bricks.network.computers.channels.guaranteed.splitter.tests;

import static basis.environments.Environments.my;
import static java.lang.String.format;

import java.nio.ByteBuffer;

import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitters;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Producer;
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
	
	
	@Test(timeout=1000, expected=IllegalArgumentException.class)
	public void packetTooBig() {
		final String largeText = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh " +
				"euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
				"minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut " +
				"aliquip ex eax..";
		
		Producer<ByteBuffer> splitter = subject.newSplitter(new Producer<ByteBuffer>() {  @Override public ByteBuffer produce() {
			return ByteBuffer.wrap(largeText.getBytes());
		}}, 1);
		
		splitter.produce();
	}
	

	private void splitAndJoin(final String expected, final int maxPieceSize) {
		Producer<ByteBuffer> sender = new Producer<ByteBuffer>() { @Override public ByteBuffer produce() {
			return ByteBuffer.wrap(expected.getBytes());
		}};
		
		RefLatch<ByteBuffer> receiver = new RefLatch<>();
		
		final Producer<ByteBuffer> splitter = subject.newSplitter(sender, maxPieceSize);
		final Consumer<ByteBuffer> joiner = subject.newJoiner(receiver);
		
		my(Threads.class).startStepping("Pipe", new Closure() { @Override public void run() {
			ByteBuffer piece = splitter.produce();
			assertTrue(format("Packet \"%s\" should have less than %s bytes", new String(piece.array()), maxPieceSize), piece.remaining() <= maxPieceSize);
			joiner.consume(piece);
		}});
		
		ByteBuffer actual = receiver.waitAndGet();
		byte[] bytes = new byte[actual.remaining()];
		actual.get(bytes);

		assertEquals(expected, new String(bytes));
	}
	
}
