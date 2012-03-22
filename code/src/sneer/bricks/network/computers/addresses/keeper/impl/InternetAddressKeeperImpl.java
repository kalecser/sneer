package sneer.bricks.network.computers.addresses.keeper.impl;

import static basis.environments.Environments.my;
import basis.lang.exceptions.Refusal;
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
			
			try {
				put(contact, (String)address[1], (Integer)address[2]);
			} catch (Refusal e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	
	private void remove(InternetAddress address) {
		_addresses.remove(address);
		save();
	}

	
	@Override
	public SetSignal<InternetAddress> addresses() {
		return _addresses.output();
	}

	@Override
	public void put(Contact contact, String host, int port) throws Refusal { //Implement Handle contact removal.
		if (!isNewAddress(contact, host, port)) return;
		
		InternetAddress addr = new InternetAddressImpl(contact, host, port);
		_addresses.add(addr);
		save();
	}

	private boolean isNewAddress(Contact contact, String host, int port) {
		InternetAddress addr = get(contact);
		if (addr == null) return true;
		if (!addr.host().equals(host)) return true;
		if (addr.port().currentValue() != port) return true;
		return false;
	}

	
	private void save() {
		Store.save(_addresses.output().currentElements());
	}

	
	@Override
	public InternetAddress get(Contact contact) {
		for (InternetAddress addr : _addresses.output())
			if(addr.contact().equals(contact)) return addr;
		return null;
	}

	
	@Override
	public void remove(Contact contact) {
		InternetAddress a = get(contact);
		if (a == null) return;
		remove(a);
	}	
}
