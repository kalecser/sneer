package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.util.Arrays;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.connections.Call;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.notifiers.Source;
import basis.lang.CacheMap;
import basis.lang.Consumer;
import basis.lang.Functor;

class UdpConnectionManagerImpl implements UdpConnectionManager{

	private static final UdpByteConnection[] EMPTY_ARRAY = new UdpByteConnection[0];
	CacheMap<Contact, UdpByteConnection> connectionsByContact = CacheMap.newInstance();
	private Consumer<DatagramPacket> sender;
	
	private final Functor<Contact, UdpByteConnection> newByteConnection = new Functor<Contact, UdpByteConnection>( ) {  @Override public UdpByteConnection evaluate(Contact contact) {
		return new UdpByteConnection(sender, contact);
	}};
	
	@SuppressWarnings("unused") private WeakContract refToAvoidGC = my(Timer.class).wakeUpEvery(UdpConnectionManager.KEEP_ALIVE_PERIOD, new Runnable() { @Override public void run() {
		keepAlive();
	}}); 
	
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
		byte[] seal = Arrays.copyOf(packet.getData(), Seal.SIZE_IN_BYTES);
		Contact contact = my(ContactSeals.class).contactGiven(new Seal(seal));
		if (contact == null) return;
		connectionFor(contact).handle(packet, Seal.SIZE_IN_BYTES);
		
	}

	@Override
	public void initSender(Consumer<DatagramPacket> sender) {
		if (this.sender != null) throw new IllegalStateException();
		this.sender = sender;
	}

	private void keepAlive() {
		for (UdpByteConnection connection : connectionsByContact.values().toArray(EMPTY_ARRAY))
			connection.keepAlive();
	}

}
