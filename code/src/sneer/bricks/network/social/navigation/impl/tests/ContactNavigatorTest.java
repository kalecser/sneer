package sneer.bricks.network.social.navigation.impl.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.navigation.ContactNavigator;
import sneer.bricks.network.social.navigation.ContactOfContact;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.util.concurrent.Latch;

public class ContactNavigatorTest extends BrickTestWithTuples {
	
	private final ContactNavigator subject = my(ContactNavigator.class);
	protected Seal remoteSeal;
	
	
	@Test(timeout=4000)
	public void requestContacts() {
		Environments.runWith(remote(), new Closure() { @Override public void run() {
			remoteSeal = ownSeal();			
			my(ContactNavigator.class);
		}});
		
		final Latch latch = new Latch();
				
		subject.searchContactsOf(remoteSeal, new Consumer<ContactOfContact>() { @Override public void consume(ContactOfContact value) {
			if (!value.nick.equals("Local Friend")) throw new IllegalStateException(value.nick);
			if (latch.isOpen()) throw new IllegalStateException(value.nick);
			latch.open();
		}});
		
		latch.waitTillOpen();
	}

	
	private Seal ownSeal() {
		return my(OwnSeal.class).get().currentValue();
	}

}
