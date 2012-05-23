package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Closure;
import basis.lang.Consumer;

class UdpByteConnection implements ByteConnection {
	
	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	private Consumer<? super byte[]> receiver;
	private final Consumer<DatagramPacket> sender;
	private final Contact contact;
	private final ConnectionMonitor monitor;

	UdpByteConnection(Consumer<DatagramPacket> sender, Contact contact) {
		this.sender = sender;
		this.contact = contact;
		monitor = new ConnectionMonitor();
		hail();
	}

	@Override
	public Signal<Boolean> isConnected() {
		return monitor.isConnected();
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
		monitor.handleSighting(packet.getSocketAddress());
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

	boolean send(byte[] payload) {
		if(sender == null) return false;
		
		byte[] ownSeal = ownSealBytes();
		byte[] data = my(Lang.class).arrays().concat(ownSeal, payload); //Optimize: Reuse array.
		
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
		SocketAddress addr = my(SightingKeeper.class).get(contact);
		return addr != null ? addr : monitor.lastSighting();
	}

	private byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}
	
	public void keepAlive() {
		hail();
		monitor.keepAlive();
	}

	private void hail() {
		send(EMPTY_BYTE_ARRAY);
	}
}
