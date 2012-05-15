package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.addresses.keeper.InternetAddress;
import sneer.bricks.network.computers.addresses.keeper.InternetAddressKeeper;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import basis.lang.Closure;
import basis.lang.Consumer;

class UdpByteConnection implements ByteConnection {

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	private Register<Boolean> isConnected = my(Signals.class).newRegister(false);
	private Consumer<? super byte[]> receiver;
	private final Consumer<DatagramPacket> sender;
	private final Contact contact;
	private SocketAddress lastPeerSighting;
	@SuppressWarnings("unused") private WeakContract refToAvoidGC;

	UdpByteConnection(Consumer<DatagramPacket> sender, Contact contact) {
		this.sender = sender;
		this.contact = contact;
		
		refToAvoidGC = my(Timer.class).wakeUpNowAndEvery(10000, new Runnable() { @Override public void run() {
			hail();
		}});
	}

	@Override
	public Signal<Boolean> isConnected() {
		return isConnected.output();
	}

	@Override
	public void initCommunications(final PacketScheduler scheduler, Consumer<? super byte[]> receiver) {
		if (this.receiver != null) throw new IllegalStateException();
		this.receiver = receiver;
		my(Threads.class).startStepping("ByteConnection", new Closure() { @Override public void run() {
			tryToSendPacketFor(scheduler);
		}});
	}
	
	
	void handle(DatagramPacket packet, int offset) {
		isConnected.setter().consume(true);
		lastPeerSighting = packet.getSocketAddress();
		if (receiver == null) return;
		receiver.consume(payload(packet.getData(), offset));
	}

	
	private byte[] payload(byte[] data, int offset) {
		return Arrays.copyOfRange(data, offset, data.length);
	}

	private void tryToSendPacketFor(PacketScheduler scheduler) {
		byte[] payload = scheduler.highestPriorityPacketToSend();
		if (send(payload))
			scheduler.previousPacketWasSent();
	}

	private boolean send(byte[] payload) {
		if(sender == null) return false;
		
		byte[] ownSeal = ownSealBytes();
		byte[] data = my(Lang.class).arrays().concat(ownSeal, payload);
		
		DatagramPacket packet = packetFor(data);
		if(packet == null) return false;
		
		sender.consume(packet);
		return true;
	}

	private DatagramPacket packetFor(byte[] data) {
		SocketAddress addr = peerAddress();
		if (addr == null) return null;
		try {
			return new DatagramPacket(data, data.length, addr); //Optimize: reuse DatagramPacket
		} catch (SocketException e) {
			my(ExceptionLogger.class).log(e);
			return null;
		}
	}
	
	private SocketAddress peerAddress() {
		InternetAddress addr = my(InternetAddressKeeper.class).get(contact);
		if(addr != null) return new InetSocketAddress(addr.host(), addr.port().currentValue());
		return lastPeerSighting;
	}

	private byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}
	
	private void hail() {
		send(EMPTY_BYTE_ARRAY);
	}

}
