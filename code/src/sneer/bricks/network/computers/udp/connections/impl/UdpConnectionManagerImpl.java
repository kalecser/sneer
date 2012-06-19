package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Source;
import basis.lang.CacheMap;
import basis.lang.Consumer;
import basis.lang.Functor;

class UdpConnectionManagerImpl implements UdpConnectionManager{

	CacheMap<Contact, UdpByteConnection> connectionsByContact = CacheMap.newInstance();
	
	private final Functor<Contact, UdpByteConnection> newByteConnection = new Functor<Contact, UdpByteConnection>( ) {  @Override public UdpByteConnection evaluate(Contact contact) {
		return new UdpByteConnection(contact);
	}};
	
	@Override
	public UdpByteConnection connectionFor(Contact contact) {
		return connectionsByContact.get(contact, newByteConnection);
	}

	@Override
	public void closeConnectionFor(Contact contact) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public Source<Call> unknownCallers() {
		return new Source<Call>() {
			@Override
			public WeakContract addPulseReceiver(Runnable pulseReceiver) {
				return null;
			}

			@Override
			public WeakContract addReceiver(Consumer<? super Call> receiver) {
				return null;
			}
		};
	}

	@Override
	public void handle(DatagramPacket packet) {
		my(Logger.class).log("Packet Received");
		if (packet.getLength() < Seal.SIZE_IN_BYTES + 1) return;
		ByteBuffer data = ByteBuffer.wrap(packet.getData());
		
		PacketType type = type(data.get());
		if (type == null) return;
		
		my(Logger.class).log("Packet Received ", type);
		if (type == PacketType.Stun) 
			my(StunClient.class).handle(data);
		else {			
			byte[] seal = new byte[Seal.SIZE_IN_BYTES];
			data.get(seal);
			Contact contact = my(ContactSeals.class).contactGiven(new Seal(seal));
			if (contact == null) return;
			connectionFor(contact).handle(type, (InetSocketAddress) packet.getSocketAddress(), data);
		}
	}

	
	static private PacketType type(byte i) {
		if (i < 0) return null;
		if (i >= PacketType.values().length) return null;
		return PacketType.values()[i];
	}

}
