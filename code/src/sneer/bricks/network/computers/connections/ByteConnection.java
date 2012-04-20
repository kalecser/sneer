package sneer.bricks.network.computers.connections;

import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.Signal;

public interface ByteConnection {

	public interface PacketScheduler {
		byte[] highestPriorityPacketToSend();
		void previousPacketWasSent();
	}
	
	Signal<Boolean> isConnected();

	void initCommunications(PacketScheduler sender, Consumer<byte[]> receiver);

	
}