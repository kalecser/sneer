package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;

import java.util.Arrays;

import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import basis.lang.Consumer;

class UdpByteConnection implements ByteConnection {

	private Register<Boolean> isConnected = my(Signals.class).newRegister(false);
	private Consumer<byte[]> receiver;

	@Override
	public Signal<Boolean> isConnected() {
		return isConnected.output();
	}

	@Override
	public void initCommunications(PacketScheduler sender, Consumer<byte[]> receiver) {
		if (this.receiver != null) throw new IllegalStateException();
		this.receiver = receiver;
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
