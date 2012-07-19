package sneer.bricks.network.computers.addresses.contacts.impl;

import static basis.environments.Environments.my;

import java.net.InetSocketAddress;

import basis.lang.Closure;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.network.computers.addresses.own.host.OwnHost;
import sneer.bricks.network.computers.addresses.own.port.OwnPort;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class ContactAddress {

	private final Contact contact;
	private final Register<InetSocketAddress> register = my(Signals.class).newRegister(null);
	@SuppressWarnings("unused") private final WeakContract ref1, ref2;

	
	public ContactAddress(Contact contact) {
		this.contact = contact;
		Closure updater = new Closure() { @Override public void run() {
			update();
		}};
		ref1 = host().addPulseReceiver(updater);
		ref2 = port().addPulseReceiver(updater);
	}


	private void update() {
		String host = host().currentValue();
		Integer port = port().currentValue();
		register.setter().consume(
			host == null || port == null
				? null
				: new InetSocketAddress(host, port));
	}

	
	Signal<InetSocketAddress> output() {
		return my(WeakReferenceKeeper.class).keep(register.output(), this);
	}

	
	private Signal<Integer> port() {
		return my(Attributes.class).attributeValueFor(contact, OwnPort.class, Integer.class);
	}
	
	
	private Signal<String> host() {
		return my(Attributes.class).attributeValueFor(contact, OwnHost.class, String.class);
	}
	
}
