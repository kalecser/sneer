package sneer.bricks.network.computers.udp.packet.impl;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.packet.PacketSplitter;

class PacketSplitterImpl implements PacketSplitter {

	@Override
	public PacketScheduler splitScheduler(PacketScheduler scheduler, int payloadSize) {
		return new SequenceScheduler(scheduler, payloadSize);
	}

}
