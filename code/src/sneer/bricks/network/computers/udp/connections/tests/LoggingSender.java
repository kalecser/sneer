package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;
import static org.junit.Assert.assertArrayEquals;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
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

	private String toString(byte[] data) {
		byte[] payload = copyToEnd(data, 1);
		
		if (data[0] == 0)			
			return "hail " + ByteBuffer.wrap(payload).getLong();
				
		if (data[0] != 1)
			throw new IllegalStateException("Unknown packet type");
		
		return new String(payload);
	}

	public Signal<String> history() {
		return packetHistory.output();
	}

	private static byte[] copyToEnd(byte[] data, int offset) {
		return Arrays.copyOfRange(data, offset, data.length);
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
		byte[] seal = Arrays.copyOf(bytes, Seal.SIZE_IN_BYTES);
		byte[] payload = copyToEnd(bytes, Seal.SIZE_IN_BYTES);
		assertArrayEquals(ownSealBytes(), seal);
		String current = packetHistory.output().currentValue();
		String packet1 = "| " + toString(payload) + ",to:" + packet.getAddress().getHostAddress() + ",port:" + packet.getPort();
		packetHistory.setter().consume(current + packet1);
		packetHistorySet.add(packet1);
	}

	@Override
	public void init(Consumer<DatagramPacket> sender) {
		
	}

}