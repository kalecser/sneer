package sneer.bricks.network.computers.udp.packet.impl;

import sneer.bricks.network.computers.udp.packet.PacketSplitter;
import sneer.bricks.network.computers.udp.packet.PacketSplitters;

class PacketSplittersImpl implements PacketSplitters {

	@Override
	public PacketSplitter newInstance(int maxPieceSize) {
		return new PacketSplitterImpl(maxPieceSize);
	}

}
