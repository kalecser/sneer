package sneer.bricks.network.computers.udp.connections.impl;

import static basis.environments.Environments.my;
import sneer.bricks.network.computers.connections.ByteConnection;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import basis.lang.Consumer;

class UdpByteConnection implements ByteConnection {

	private Register<Boolean> isConnected = my(Signals.class).newRegister(false);

	@Override
	public Signal<Boolean> isConnected() {
		return isConnected.output();
	}

	@Override
	public void initCommunications(PacketScheduler sender, Consumer<byte[]> receiver) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	void becomeConnected() {
		isConnected.setter().consume(true);
	}

}
