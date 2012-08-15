package sneer.bricks.network.computers.channels.guaranteed.splitter.impl;

import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitter;
import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitters;

class PacketSplittersImpl implements PacketSplitters {

	@Override
	public PacketSplitter newInstance(int maxPieceSize) {
		return new PacketSplitterImpl(maxPieceSize);
	}

}
