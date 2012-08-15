package sneer.bricks.network.computers.channels.tests;

import static basis.environments.Environments.my;

import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.own.port.OwnPort;
import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.Channels;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.tests.adapters.impl.utils.network.udp.impl.InProcessUdpNetworkImpl;
import basis.brickness.testsupport.Bind;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.ByRef;
import basis.lang.Closure;
import basis.lang.Producer;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.RefLatch;

public class ChannelsTest extends BrickTestBase {

	private final Channels subject = my(Channels.class);
	
	@Bind private final UdpNetwork network = new InProcessUdpNetworkImpl();
	
	
	@Ignore
	@Test(timeout = 2000)
	public void createControl() throws Exception {
		my(Attributes.class).myAttributeSetter(OwnPort.class).consume(10000);
		final Seal sealA = ownSeal();
		
		final ByRef<Seal> sealB = ByRef.newInstance();
		Environment remoteEnv = this.newTestEnvironment(network);
		Environments.runWith(remoteEnv, new Closure() { @Override public void run() {
			my(Attributes.class).myAttributeSetter(OwnPort.class).consume(10001);
			Contact a = newContact("A", sealA);
			Channel channelB = my(Channels.class).createControl(a);
			channelB.open(new Producer<ByteBuffer>() { @Override public ByteBuffer produce() {
				return ByteBuffer.wrap("Hello".getBytes());
			}}, null);
			sealB.value = ownSeal();
		}});
		
		Contact b = newContact("B", sealB.value);
		
		RefLatch<ByteBuffer> latch = new RefLatch<>();
		Channel channelA = subject.createControl(b);
		channelA.open(new Producer<ByteBuffer>() { @Override public ByteBuffer produce() {
			my(Threads.class).sleepWithoutInterruptions(20000);
			throw new IllegalStateException();
		}}, latch);
		ByteBuffer received = latch.waitAndGet();
		
		assertEquals(ByteBuffer.wrap("Hello".getBytes()), received);
	}


	Contact newContact(String nick, Seal seal) {
		Contact ret = my(Contacts.class).produceContact(nick);
		try {
			my(ContactSeals.class).put(nick, seal);
		} catch (Refusal e) {
			throw new IllegalStateException(e);
		}
		return ret;
	}


	Seal ownSeal() {
		return my(OwnSeal.class).get().currentValue();
	}
}
