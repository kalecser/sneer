package sneer.bricks.network.computers.udp.packet;

import java.nio.ByteBuffer;

import sneer.bricks.pulp.notifiers.Source;

public interface PacketSplitter {
	
	ByteBuffer[] split(ByteBuffer whole, int maxPieceSize);
	
	void join(ByteBuffer piece);
	Source<ByteBuffer> lastJoinedPacket();

}
