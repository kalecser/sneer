package sneer.tests.freedom5;

import static basis.environments.Environments.my;

import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.computers.channels.Channel;
import sneer.tests.SovereignFunctionalTestBase;
import basis.lang.Producer;
import basis.util.concurrent.RefLatch;

public class Freedom5TestChannels extends SovereignFunctionalTestBase {

	@Ignore
	@Test (timeout = 13000)
	public void openControlChannel() {

		Channel channelB = b().openControlChannel(a().ownName());
		channelB.open(new Producer<ByteBuffer>() { @Override public ByteBuffer produce() {
			return ByteBuffer.wrap("Hello".getBytes());
		}}, null);
		
		RefLatch<ByteBuffer> latch = new RefLatch<>();
		Channel channelA = a().openControlChannel(b().ownName());
		channelA.open(new Producer<ByteBuffer>() { @Override public ByteBuffer produce() {
			my(Threads.class).sleepWithoutInterruptions(20000);
			throw new IllegalStateException();
		}}, latch);
		ByteBuffer received = latch.waitAndGet();
		
		assertEquals(ByteBuffer.wrap("Hello".getBytes()), received);
	}

}