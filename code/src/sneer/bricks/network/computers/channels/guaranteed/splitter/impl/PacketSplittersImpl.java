package sneer.bricks.network.computers.channels.guaranteed.splitter.impl;


import java.nio.ByteBuffer;

import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitters;
import basis.lang.Consumer;
import basis.lang.Producer;

class PacketSplittersImpl implements PacketSplitters {
	

	@Override
	public Producer<ByteBuffer> newSplitter(final Producer<? extends ByteBuffer> largePacketSender, final int maxPieceSize) {
		return new Splitter(largePacketSender, maxPieceSize);
	}
	
	
	@Override
	public Consumer<ByteBuffer> newJoiner(final Consumer<? super ByteBuffer> largePacketReceiver) {
		return new Joiner(largePacketReceiver); 
	}

}
