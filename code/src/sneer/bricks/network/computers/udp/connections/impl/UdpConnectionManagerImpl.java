package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Hail;
import static sneer.bricks.network.computers.udp.connections.UdpPacketType.Stun;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.computers.udp.connections.UdpPacketType;
import sneer.bricks.network.computers.udp.holepuncher.client.StunClient;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.notifiers.Source;
import basis.lang.CacheMap;
import basis.lang.Functor;

class UdpConnectionManagerImpl implements UdpConnectionManager {

	static private Charset ENCODING = Charset.forName("UTF-8");
	CacheMap<Contact, UdpByteConnection> connectionsByContact = CacheMap.newInstance();
	
	private final Functor<Contact, UdpByteConnection> newByteConnection = new Functor<Contact, UdpByteConnection>( ) {  @Override public UdpByteConnection evaluate(Contact contact) {
		return new UdpByteConnection(contact);
	}};

	private Notifier<Call> unknownCallers = my(Notifiers.class).newInstance();
	
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
		return unknownCallers.output();
	}

	@Override
	public void handle(DatagramPacket packet) {
		if (packet.getLength() < Seal.SIZE_IN_BYTES + 1) return;
		ByteBuffer buf = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
		
		UdpPacketType type = type(buf.get());
		if (type == null) return;
		
		my(Logger.class).log("Packet Received ", type);
		if (type == Stun) {
			my(StunClient.class).handle(buf);
			return;
		}
		
		byte[] seal = new byte[Seal.SIZE_IN_BYTES];
		buf.get(seal);
		Seal sendersSeal = new Seal(seal);
		Contact contact = my(ContactSeals.class).contactGiven(sendersSeal);
		if (contact == null) {
			handleFromUnknownSender(type, sendersSeal, buf);
			return;
		}
		
		connectionFor(contact).handle(type, (InetSocketAddress) packet.getSocketAddress(), buf);
	}

	
	private void handleFromUnknownSender(UdpPacketType type, final Seal sendersSeal, final ByteBuffer buf) {
		if (type != Hail) return;
		unknownCallers.notifyReceivers(new Call() {

			private final String callerName = callerName(buf);

			private String callerName(ByteBuffer buf) {
				buf.getLong(); //Skip the timestamp;
				buf.position(buf.position() + OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES); // Skip the public key
				byte[] ret = new byte[buf.remaining()];
				buf.get(ret);
				return new String(ret, ENCODING);
			}

			@Override
			public Seal callerSeal() {
				return sendersSeal;
			}

			@Override
			public String callerName() {
				return callerName;
			}});
	}
	

	static private UdpPacketType type(byte i) {
		if (i < 0) return null;
		if (i >= UdpPacketType.values().length) return null;
		return UdpPacketType.values()[i];
	}

}
