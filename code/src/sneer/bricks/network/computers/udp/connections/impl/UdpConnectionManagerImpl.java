package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
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

	private static final UdpByteConnection[] EMPTY_ARRAY = new UdpByteConnection[0];
	
	CacheMap<Contact, UdpByteConnection> connectionsByContact = CacheMap.newInstance();
	
	@SuppressWarnings("unused") private final WeakContract refToAvoidGC = my(Timer.class).wakeUpEvery(UdpConnectionManager.KEEP_ALIVE_PERIOD, new Runnable() { @Override public void run() {
		keepAlive();
	}});
	
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
		if (packet.getLength() < Seal.SIZE_IN_BYTES + 1) return;
		
		ByteBuffer data = ByteBuffer.wrap(packet.getData());
		PacketType type = UdpConnectionManager.PacketType.values()[data.get()];
		if (type == PacketType.Stun) 
			my(StunClient.class).handle(data);
		else {			
			byte[] seal = new byte[Seal.SIZE_IN_BYTES];
			data.get(seal);
			Contact contact = my(ContactSeals.class).contactGiven(new Seal(seal));
			if (contact == null) return;
			connectionFor(contact).handle(type, packet.getSocketAddress(), data);
		}
	}

	private void keepAlive() {
		for (UdpByteConnection connection : connectionsByContact.values().toArray(EMPTY_ARRAY))
			connection.keepAlive();
	}

}
