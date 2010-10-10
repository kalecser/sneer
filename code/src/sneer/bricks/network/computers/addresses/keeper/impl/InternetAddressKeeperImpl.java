package sneer.bricks.network.computers.addresses.keeper.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;

class InternetAddressKeeperImpl implements InternetAddressKeeper {

	private final Contacts _contactManager = my(Contacts.class);
	private final SetRegister<InternetAddress> _addresses = my(CollectionSignals.class).newSetRegister();
	
	InternetAddressKeeperImpl(){
		restore();
	}

	private void restore() {
		for (Object[] address : Store.restore()) {
			Contact contact = _contactManager.contactGiven((String)address[0]);
			if (contact == null) continue;
			
			add(contact, (String)address[1], (Integer)address[2]);
		}
	}
	
	@Override
	public void remove(InternetAddress address) {
		_addresses.remove(address);
		save();
	}

	@Override
	public SetSignal<InternetAddress> addresses() {
		return _addresses.output();
	}

	@Override
	public void add(Contact contact, String host, int port) { //Implement Handle contact removal.
		if (!isNewAddress(contact, host, port)) return;
		
		InternetAddress addr = new InternetAddressImpl(contact, host, port);
		_addresses.add(addr);
		save();
	}

	private boolean isNewAddress(Contact contact, String host, int port) {
		for (InternetAddress addr : _addresses.output())
			if(addr.contact().equals(contact) 
					&& addr.host().equals(host)
					&& addr.port().currentValue() == port)
				return false;
		
		return true;
	}

	private void save() {
		Store.save(_addresses.output().currentElements());
	}	
}
