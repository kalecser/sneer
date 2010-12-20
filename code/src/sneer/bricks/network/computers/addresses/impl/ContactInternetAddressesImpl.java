package sneer.bricks.network.computers.addresses.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.addresses.ContactInternetAddresses;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.sockets.connections.Sighting;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.lang.Consumer;

class ContactInternetAddressesImpl implements ContactInternetAddresses {

	SetRegister<InternetAddress> _addresses = my(CollectionSignals.class).newSetRegister();
	
	
	@SuppressWarnings("unused")
	private final WeakContract _refToAvoidGc = my(RemoteTuples.class).addSubscription(Sighting.class, new Consumer<Sighting>() { @Override public void consume(Sighting sighting) {
		handle(sighting);
	}});

	
	@SuppressWarnings("unused")
	private final WeakContract _refToAvoidGc2 = my(InternetAddressKeeper.class).addresses().addReceiver(new Consumer<CollectionChange<InternetAddress>>() { @Override public void consume(CollectionChange<InternetAddress> change) {
		_addresses.change(change);
	}});
	
	
	@Override
	public SetSignal<InternetAddress> addresses() {
		return _addresses.output();
	}

	
	private void handle(final Sighting sighting) {
		final Contact contact = my(ContactSeals.class).contactGiven(sighting.peersSeal);
		if (contact == null) return;
		
		_addresses.add(new InternetAddressImpl(contact, sighting));
	}

}
