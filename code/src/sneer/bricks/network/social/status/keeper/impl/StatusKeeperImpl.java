package sneer.bricks.network.social.status.keeper.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.status.keeper.StatusKeeper;
import sneer.bricks.network.social.status.protocol.StatusFactory;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class StatusKeeperImpl implements StatusKeeper {

	private CacheMap<Contact, Register<String>> _statusByContact = CacheMap.newInstance();

	@Override
	public Signal<String> status(Contact contact) {
		return statusRegister(contact).output();
	}

	@Override
	public void setStatus(Contact contact, String status) {
		statusRegister(contact).setter().consume(status);
	}

	private Register<String> statusRegister(Contact contact) throws RuntimeException {
		return _statusByContact.get(contact, new Producer<Register<String>>() { @Override public Register<String> produce() throws RuntimeException {
			return my(Signals.class).newRegister(my(StatusFactory.class).defaultValue());
		}});
	}

}
