package sneer.bricks.network.computers.channels.guaranteed.splitter;

import basis.brickness.Brick;

@Brick
public interface PacketSplitters {
	
	PacketSplitter newInstance(int maxPieceSize);

}
