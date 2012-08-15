package sneer.bricks.network.computers.channels.guaranteed.splitter;

import java.nio.ByteBuffer;

import basis.brickness.Brick;
import basis.lang.Consumer;
import basis.lang.Producer;

@Brick
public interface PacketSplitters {
	
	static final int MAX_PIECES = 256;
	
	Producer<ByteBuffer> newSplitter(Producer<ByteBuffer> largePacketSender, int maxPieceSize);
	
	Consumer<ByteBuffer> newJoiner(Consumer<ByteBuffer> largePacketReceiver);
	
}
