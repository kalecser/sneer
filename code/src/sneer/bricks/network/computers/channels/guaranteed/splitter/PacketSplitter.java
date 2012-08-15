package sneer.bricks.network.computers.channels.guaranteed.splitter;

import java.nio.ByteBuffer;

import sneer.bricks.pulp.notifiers.Source;

public interface PacketSplitter {
	
	static final int MAX_PIECES = 256;
	
	ByteBuffer[] split(ByteBuffer whole);
	
	void join(ByteBuffer piece);
	Source<ByteBuffer> lastJoinedPacket();
	
}
