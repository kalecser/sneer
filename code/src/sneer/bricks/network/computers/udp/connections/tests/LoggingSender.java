package sneer.bricks.network.computers.udp.connections.tests;

import static basis.environments.Environments.my;
import static org.junit.Assert.assertArrayEquals;

import java.net.DatagramPacket;
import java.util.Arrays;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import basis.lang.Consumer;

final class LoggingSender implements Consumer<DatagramPacket> {
	
	private Register<String> packetHistory = my(Signals.class).newRegister("");

	@Override public void consume(DatagramPacket packetToSend) {
		byte[] bytes = packetToSend.getData();
		byte[] seal = Arrays.copyOf(bytes, Seal.SIZE_IN_BYTES);
		byte[] payload = payload(bytes, Seal.SIZE_IN_BYTES);
		assertArrayEquals(ownSealBytes(), seal);
		String current = packetHistory.output().currentValue();
		packetHistory.setter().consume(
			current
			+ "| " + toString(payload)
			+ ",to:" + packetToSend.getAddress().getHostAddress()
			+ ",port:" + packetToSend.getPort()
		);
	}

	private String toString(byte[] payload) {
		String ret = new String(payload);
		return ret.isEmpty() ? "<empty>" : ret;
	}

	Signal<String> history() {
		return packetHistory.output();
	}

	private static byte[] payload(byte[] data, int offset) {
		return Arrays.copyOfRange(data, offset, data.length);
	}

	private static byte[] ownSealBytes() {
		return my(OwnSeal.class).get().currentValue().bytes.copy();
	}

}