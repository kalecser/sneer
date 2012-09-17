package sneer.bricks.network.social.navigation.impl;

import static basis.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.navigation.ContactNavigator;
import sneer.bricks.network.social.navigation.ContactOfContact;
import sneer.bricks.network.social.navigation.ContactsRequest;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.Consumer;

public class ContactNavigatorImpl implements ContactNavigator {
	
	private WeakContract ref;
	@SuppressWarnings("unused")
	private WeakContract ref2;
	
	{
		ref2 = my(RemoteTuples.class).addSubscription(ContactsRequest.class, new Consumer<ContactsRequest>(){

			@Override
			public void consume(ContactsRequest request) {
				SetSignal<Contact> contacts = my(Contacts.class).contacts();
				for (Contact each : contacts.currentElements()){
					ContactOfContact tuple = new ContactOfContact(nick(each), seal(each), request.publisher);
					my(TupleSpace.class).add(tuple);
				}
			}

			private Seal seal(Contact each) {
				return my(ContactSeals.class).sealGiven(each).currentValue();
			}

			private String nick(Contact each) {
				return each.nickname().currentValue();
			}});
	}

	@Override
	public void searchContactsOf(final Seal adressee, final Consumer<ContactOfContact> consumer) {
		TupleSpace tuplespace = my(TupleSpace.class);
		if (ref != null) ref.dispose();
		ref = tuplespace.addSubscription(ContactOfContact.class, new Consumer<ContactOfContact>(){  @Override public void consume(ContactOfContact value) {
			if (value.publisher.equals(adressee)) 
				consumer.consume(value);
		}});
		tuplespace.add(new ContactsRequest(adressee));
	}

}
