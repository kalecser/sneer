package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.udp.sender.UdpSender;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import basis.lang.Closure;
import basis.lang.Consumer;

class UdpByteConnection implements ByteConnection {
	
	private Consumer<? super byte[]> receiver;
	private final UdpSender sender = my(UdpSender.class);
	private final Contact contact;
	private final ConnectionMonitor monitor = new ConnectionMonitor();
	private byte hailSequence = 0;

	@SuppressWarnings("unused") private final WeakContract refToAvoidGC;

	UdpByteConnection(Contact contact) {
		this.contact = contact;
		refToAvoidGC = my(SightingKeeper.class).sightingsOf(contact).addPulseReceiver(new Closure() { @Override public void run() {
			keepAlive();
		}});
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
	
	private void tryToSendPacketFor(PacketScheduler scheduler) {
		byte[] payload = scheduler.highestPriorityPacketToSend();
		if (send(payload, monitor.lastSighting()))
			scheduler.previousPacketWasSent();
	}
	
	void handle(DatagramPacket packet, int offset) {
		SocketAddress sighting = packet.getSocketAddress();
		my(SightingKeeper.class).keep(contact, sighting);

		byte[] bytes = packet.getData();
		
		if(bytes[offset] == 0) {
			monitor.handleSighting(sighting, bytes[offset + 1]);
			return;
		}
		
		if (receiver == null) return;
		receiver.consume(payload(bytes, offset + 1));
	}

	private byte[] payload(byte[] data, int offset) {
		return Arrays.copyOfRange(data, offset, data.length);
	}

	private boolean send(byte[] payload, SocketAddress peerAddress) {
		byte[] ownSeal = ownSealBytes();
		byte[] dataIdentifier = new byte[] { 1 };
		
		byte[] data = my(Lang.class).arrays().concat(ownSeal, dataIdentifier);
		data = my(Lang.class).arrays().concat(data, payload); //Optimize: Reuse array.
		
		DatagramPacket packet = packetFor(data, peerAddress);
		if(packet == null) return false;
		
		sender.send(packet);
		return true;
	}

	private static DatagramPacket packetFor(byte[] data, SocketAddress peerAddress) {
		if (peerAddress == null) return null;
		try {
			return new DatagramPacket(data, data.length, peerAddress); //Optimize: reuse DatagramPacket
		} catch (SocketException e) {
			my(ExceptionLogger.class).log(e);
			return null;
		}
	}
	
	private static byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}
	
	void keepAlive() {
		hail(my(SightingKeeper.class).sightingsOf(contact));
		monitor.disconnectIfIdle();
	}
	
	private void hail(Iterable<SocketAddress> addrs) {
		byte[] bytes = new byte[] { 0, hailSequence++ };
		for (SocketAddress addr : addrs)
			send(bytes, addr);
	}

}
