package sneer.bricks.network.social.navigation.impl.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.navigation.ContactNavigator;
import sneer.bricks.network.social.navigation.ContactOfContact;
import sneer.bricks.network.social.navigation.ContactsRequest;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.exceptions.Refusal;
import basis.util.concurrent.Latch;

public class ContactNavigatorTest extends BrickTestWithTuples {
	
	private final ContactNavigator subject = my(ContactNavigator.class);
	protected Seal seal;
	
	@Test(timeout=4000)
	public void searchReply(){
		final Seal ownSeal = ownSeal();
		
		Environments.runWith(remote(), new Closure(){  @Override public void run() {
			seal = ownSeal();			
		}});
		
		final Latch latch = new Latch();
				
		subject.searchContactsOf(seal, new Consumer<ContactOfContact>(){  @Override public void consume(ContactOfContact value) {
			latch.open();
		}});
					
		Environments.runWith(remote(), new Closure(){  @Override public void run() {
			my(TupleSpace.class).add(new ContactOfContact("Fred", new Seal(new byte[]{1}), ownSeal));
		}});
		
		latch.waitTillOpen();
	}
	
	@Test(timeout=4000)
	public void searchRequest() throws Refusal{				
		final Latch latch = new Latch();
		
		@SuppressWarnings("unused")
		WeakContract ref = my(TupleSpace.class).addSubscription(ContactOfContact.class, new Consumer<ContactOfContact>(){  @Override public void consume(ContactOfContact value) {
			if (value.nick.equals("Neide"))
				latch.open();
		}});
		
		my(Contacts.class).addContact("Neide");
		my(TupleSpace.class).add(new ContactsRequest(ownSeal()));				
				
		latch.waitTillOpen();
	}

	private Seal ownSeal() {
		return my(OwnSeal.class).get().currentValue();
	}

}
