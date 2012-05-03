package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.net.DatagramPacket;
import java.util.Arrays;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import basis.lang.Closure;
import basis.lang.Consumer;

class UdpByteConnection implements ByteConnection {

	private Register<Boolean> isConnected = my(Signals.class).newRegister(false);
	private Consumer<? super byte[]> receiver;
	private final Consumer<DatagramPacket> sender;

	UdpByteConnection(Consumer<DatagramPacket> sender) {
		this.sender = sender;
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
			byte[] ownSeal = ownSealBytes();
			byte[] payload = scheduler.highestPriorityPacketToSend();
			byte[] data = my(Lang.class).arrays().concat(ownSeal, payload);
			DatagramPacket packet = new DatagramPacket(data, data.length);//Optimize: reuse DatagramPacket
			sender.consume(packet);
			scheduler.previousPacketWasSent();
		}});
	}
	
	
	private byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}

	
	void handle(byte[] data, int offset) {
		isConnected.setter().consume(true);
		if (receiver == null) return;
		receiver.consume(payload(data, offset));
	}

	
	private byte[] payload(byte[] data, int offset) {
		return Arrays.copyOfRange(data, offset, data.length);
	}

}
