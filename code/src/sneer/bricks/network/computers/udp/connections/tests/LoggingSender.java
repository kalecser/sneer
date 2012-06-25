package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;
import static org.junit.Assert.assertArrayEquals;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

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

	private String toString(byte type, byte[] payload) {
		UdpPacketType packetType = UdpPacketType.search(type);
		if (packetType == UdpPacketType.Hail)			
			return "hail " + ByteBuffer.wrap(payload).getLong();
				
		return new String(payload);
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
		byte[] bytes = packet.getData();
		byte type = bytes[0];
		byte[] seal = Arrays.copyOfRange(bytes, 1, Seal.SIZE_IN_BYTES + 1);
		byte[] payload = Arrays.copyOfRange(bytes, Seal.SIZE_IN_BYTES + 1, packet.getLength());
		assertArrayEquals(ownSealBytes(), seal);
		String current = packetHistory.output().currentValue();
		String packet1 = "| " + toString(type, payload) + ",to:" + packet.getAddress().getHostAddress() + ",port:" + packet.getPort();
		packetHistory.setter().consume(current + packet1);
		packetHistorySet.add(packet1);
	}

	@Override
	public void init(Consumer<DatagramPacket> sender) {
		
	}

}