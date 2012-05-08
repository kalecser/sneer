package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.util.Arrays;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Source;
import basis.lang.CacheMap;
import basis.lang.Consumer;
import basis.lang.Producer;

class UdpConnectionManagerImpl implements UdpConnectionManager{

	CacheMap<Contact, UdpByteConnection> connectionsByContact = CacheMap.newInstance();
	private Consumer<DatagramPacket> sender; 

	@Override
	public UdpByteConnection connectionFor(final Contact contact) {
		return connectionsByContact.get(contact, new Producer<UdpByteConnection>( ) {  @Override public UdpByteConnection produce() {
			return new UdpByteConnection(sender, contact);
		}});
	}

	@Override
	public void closeConnectionFor(Contact contact) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public Source<Call> unknownCallers() {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void handle(DatagramPacket packet) {
		byte[] seal = Arrays.copyOf(packet.getData(), Seal.SIZE_IN_BYTES);
		Contact contact = my(ContactSeals.class).contactGiven(new Seal(seal));
		if (contact == null) return;
		connectionFor(contact).handle(packet.getData(), Seal.SIZE_IN_BYTES);
		
	}

	@Override
	public void initSender(Consumer<DatagramPacket> sender) {
		if (this.sender != null) throw new IllegalStateException();
		this.sender = sender;
	}

}
