package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;
import static org.junit.Assert.assertArrayEquals;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.udp.connections.UdpPacketType;
import sneer.bricks.network.computers.udp.sender.UdpSender;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.Consumer;

public final class LoggingSender implements UdpSender {
	
	private Register<String> packetHistory = my(Signals.class).newRegister("");
	private SetRegister<String> packetHistorySet = my(CollectionSignals.class).newSetRegister();

	private String toString(byte type, ByteBuffer buf) {
		UdpPacketType packetType = UdpPacketType.search(type);
		String ret = packetType.name() + " ";

		if (packetType == UdpPacketType.Hail) {			
			ret += buf.getLong() + " ";
			byte[] key = new byte[OwnKeys.PUBLIC_KEY_SIZE_IN_BYTES];
			buf.get(key);
			ret += "PK:" + key[0] + key[1] + " ";
		}
		
		byte[] payload = new byte[buf.remaining()];
		buf.get(payload);
		return ret + new String(payload);
	}

	public Signal<String> history() {
		return packetHistory.output();
	}

	private static byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}

	public SetSignal<String> historySet() {
		return packetHistorySet.output();
	}

	@Override
	public void send(DatagramPacket packet) {
		ByteBuffer buf = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
		byte type = buf.get();
		byte[] seal = new byte[Seal.SIZE_IN_BYTES];
		buf.get(seal);
		assertArrayEquals(ownSealBytes(), seal);
		String packetString = "| " + toString(type, buf) + ",to:" + packet.getAddress().getHostAddress() + ",port:" + packet.getPort();
		packetHistory.setter().consume(packetHistory.output().currentValue() + packetString);
		packetHistorySet.add(packetString);
	}

	@Override
	public void init(Consumer<DatagramPacket> sender) {
		
	}

}