package sneer.bricks.network.computers.channels.tests;

import static basis.environments.Environments.my;

import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.Channels;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.tests.adapters.impl.utils.network.udp.impl.InProcessUdpNetworkImpl;
import basis.brickness.testsupport.Bind;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Producer;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.RefLatch;

public class ChannelsTest extends BrickTestBase {

	private final Channels subject = my(Channels.class);
	
	@Bind private final UdpNetwork network = new InProcessUdpNetworkImpl();
	
	
	@Ignore
	@Test(timeout = 2000)
	public void createControl() {
		final Seal sealA = my(OwnSeal.class).get().currentValue();
		Contact b = my(Contacts.class).produceContact("B");
		
		Environment remoteEnv = this.newTestEnvironment(network);
		Environments.runWith(remoteEnv, new Closure() { @Override public void run() {
			Contact a = my(Contacts.class).produceContact("A");
			try {
				my(ContactSeals.class).put("A", sealA);
			} catch (Refusal e) {
				throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
			}
			Channel channelB = my(Channels.class).createControl(a);
			channelB.open(new Producer<ByteBuffer>() { @Override public ByteBuffer produce() {
				return ByteBuffer.wrap("Hello".getBytes());
			}}, null);
		}});
		
		//Seal
		
		RefLatch<ByteBuffer> latch = new RefLatch<>();
		Channel channelA = subject.createControl(b);
		channelA.open(null, latch);
		ByteBuffer received = latch.waitAndGet();
		
		assertEquals(ByteBuffer.wrap("Hello".getBytes()), received);
	}
}
