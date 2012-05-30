package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.connections.UdpConnectionManager.PacketType.Data;
import static sneer.bricks.network.computers.udp.connections.UdpConnectionManager.PacketType.Hail;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.network.computers.udp.connections.UdpConnectionManager.PacketType;
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
		if (send(Data, payload, monitor.lastSighting()))
			scheduler.previousPacketWasSent();
	}
	
	void handle(DatagramPacket packet, int offset) {
		SocketAddress sighting = packet.getSocketAddress();
		my(SightingKeeper.class).keep(contact, sighting);

		byte[] bytes = packet.getData();
		byte[] payload = copyToEnd(bytes, offset + 1);
		
		if(bytes[offset] == Hail.ordinal()) {
			long hailTimestamp = ByteBuffer.wrap(payload).getLong();
			monitor.handleHail(sighting, hailTimestamp);
			return;
		}
		
		if (receiver == null) return;
		receiver.consume(payload);
	}

	private byte[] copyToEnd(byte[] data, int offset) {
		return Arrays.copyOfRange(data, offset, data.length);
	}

	private boolean send(PacketType type, byte[] payload, SocketAddress peerAddress) {
		byte[] ownSeal = ownSealBytes();
		byte[] typeByte = new byte[] { (byte)type.ordinal() };
		
		byte[] data = my(Lang.class).arrays().concat(ownSeal, typeByte);
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
		long now = my(Clock.class).preciseTime();
		byte[] hailBytes = ByteBuffer.allocate(8).putLong(now).array(); //Optimize: Reuse buffer
		for (SocketAddress addr : addrs)
			send(Hail, hailBytes, addr);
	}

}
