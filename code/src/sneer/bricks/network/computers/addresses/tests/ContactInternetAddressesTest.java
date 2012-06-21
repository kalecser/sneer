package sneer.bricks.network.computers.addresses.tests;

import static basis.environments.Environments.my;

import java.util.Iterator;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.addresses.ContactInternetAddresses;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.computers.tcp.connections.Sighting;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import basis.brickness.testsupport.Bind;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.RefLatch;

public class ContactInternetAddressesTest extends BrickTestWithTuples {

	@Bind private final Attributes _attributes = mock(Attributes.class);

	private final ContactInternetAddresses _subject = my(ContactInternetAddresses.class);


	@Test(timeout=2000)
	public void keptAddressesAreFound() throws Refusal {
		Contact neide = my(Contacts.class).addContact("Neide");
		my(InternetAddressKeeper.class).put(neide, "10.42.10.42", 42);

		InternetAddress kept = firstKeptAddress();
		assertEquals(neide, kept.contact());
		assertEquals("10.42.10.42", kept.host());
		assertEquals(42, (int)kept.port().currentValue());
	}

	
	@Test(timeout=2000)
	public void sightingAddressesAreFound() {
		see("10.42.10.42", 8081);
		
		InternetAddress kept = firstKeptAddress();
		assertEquals(remoteContact(), kept.contact());
		assertEquals("10.42.10.42", kept.host());
		assertEquals(8081, (int)kept.port().currentValue());
	}

	
	private void see(final String ip, final int port) {
		Environments.runWith(remote(), new Closure() { @Override public void run() {
			Sighting result = new Sighting(ownSeal(), ip);
			my(TupleSpace.class).add(result);
		}});
		waitForAllDispatchingToFinish();
		
		checking(new Expectations() {{
			oneOf(_attributes).attributeValueFor(remoteContact(), OwnPort.class, Integer.class); 
			will(returnValue(my(Signals.class).constant(port)));
		}});
	}
	
	
	private InternetAddress firstKeptAddress() {
		final RefLatch<InternetAddress> latch = new RefLatch<InternetAddress>();
		_subject.addresses().addReceiver(new Consumer<CollectionChange<InternetAddress>>() { @Override public void consume(CollectionChange<InternetAddress> change) {
			Iterator<InternetAddress> it = change.elementsAdded().iterator();
			if (!it.hasNext()) return;
			latch.set(it.next());
			if (it.hasNext()) throw new IllegalStateException("More than one address found.");
		}});
		return latch.waitAndGet();
	}

	
	private Seal ownSeal() {
		return my(OwnSeal.class).get().currentValue();
	}

}
