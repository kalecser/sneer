package sneer.bricks.network.social.navigation.impl.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.navigation.ContactNavigator;
import sneer.bricks.network.social.navigation.ContactOfContact;
import basis.environments.Environments;
import basis.lang.ClosureX;
import basis.lang.Consumer;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.Latch;

public class ContactNavigatorTest extends BrickTestWithTuples {
	
	private final ContactNavigator subject = my(ContactNavigator.class);
	protected Seal remoteSeal;
	
	
	@Test(timeout=4000)
	public void requestContacts() throws Refusal{
		Environments.runWith(remote(), new ClosureX<Refusal>(){  @Override public void run() throws Refusal {
			remoteSeal = ownSeal();			
			my(Contacts.class).addContact("Neide");
			my(ContactNavigator.class);
		}});
		
		final Latch latch = new Latch();
				
		subject.searchContactsOf(remoteSeal, new Consumer<ContactOfContact>(){  @Override public void consume(ContactOfContact value) {
			if (value.nick.equals("Neide"))
				latch.open();
		}});
					
		
		latch.waitTillOpen();
	}

	
	private Seal ownSeal() {
		return my(OwnSeal.class).get().currentValue();
	}

}
