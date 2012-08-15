package sneer.bricks.network.computers.channels.guaranteed.splitter.impl;

import static sneer.bricks.network.computers.channels.guaranteed.splitter.impl.Splitter.BYTES_FOR_REMAINING_PIECES;

import java.nio.ByteBuffer;

import basis.lang.Consumer;

class Joiner implements Consumer<ByteBuffer> {
	
	private final Consumer<ByteBuffer> largePacketReceiver;
	private ByteBuffer whole;
	private int piecesToJoin = 0;

	Joiner(Consumer<ByteBuffer> largePacketReceiver) {
		this.largePacketReceiver = largePacketReceiver;
	}

	
	@Override 
	synchronized
	public void consume(ByteBuffer piece) {
		join(piece);
		if (piecesToJoin != 0) return;
		
		whole.flip();
		largePacketReceiver.consume(whole);
		whole = null;
	}
	

	private void join(ByteBuffer piece) {
		if (whole == null) {
			int pieceSize = piece.remaining();
			piecesToJoin = piece.get() & 0xFF;
			whole = ByteBuffer.allocate((1 + piecesToJoin) * pieceSize - BYTES_FOR_REMAINING_PIECES);
		} else {
			piecesToJoin--;
		}
		
		whole.put(piece);
	}
}