package sneer.bricks.network.computers.tcp.connections.originator.impl;

import static basis.environments.Environments.my;

import java.util.HashMap;
import java.util.Map;

import sneer.bricks.network.computers.addresses.contacts.tcp.ContactInternetAddresses;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.tcp.connections.originator.SocketOriginator;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import basis.lang.Consumer;

class SocketOriginatorImpl implements SocketOriginator {

	@SuppressWarnings("unused")
	private final Object _refToAvoidGC;
	private final Map<InternetAddress, OutgoingAttempt> _attemptsByAddress = new HashMap<InternetAddress, OutgoingAttempt>();
	
	
	SocketOriginatorImpl() {
		_refToAvoidGC = my(ContactInternetAddresses.class).addresses().addReceiver(new Consumer<CollectionChange<InternetAddress>>(){ @Override public void consume(CollectionChange<InternetAddress> value) {
			for (InternetAddress address : value.elementsRemoved()) 
				stopAddressing(address);
		
			for (InternetAddress address : value.elementsAdded()) 
				startAddressing(address);
		}});
	}

	
	private void startAddressing(InternetAddress address) {
		OutgoingAttempt attempt = new OutgoingAttempt(address);
		_attemptsByAddress.put(address, attempt);
	}

	
	private void stopAddressing(InternetAddress address) {
		OutgoingAttempt attempt = _attemptsByAddress.remove(address);
		attempt.crash();
	}

	
}