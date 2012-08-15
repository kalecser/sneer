package sneer.bricks.network.computers.udp.packet;

import basis.brickness.Brick;

@Brick
public interface PacketSplitters {
	
	PacketSplitter newInstance(int maxPieceSize);

}
