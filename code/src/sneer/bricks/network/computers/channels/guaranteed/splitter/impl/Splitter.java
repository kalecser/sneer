package sneer.bricks.network.computers.channels.guaranteed.splitter.impl;

import static java.lang.Math.min;
import static sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitters.MAX_PIECES;

import java.nio.ByteBuffer;

import basis.lang.Producer;

class Splitter implements Producer<ByteBuffer> {
	
	static final int BYTES_FOR_REMAINING_PIECES = 1; //Representing up to PacketSplitters.MAX_PIECES
	
	private final Producer<? extends ByteBuffer> largePacketSender;
	private final int maxPieceSize;

	private ByteBuffer whole = ByteBuffer.allocate(0);
	private int bytesPerPiece;

	
	Splitter(Producer<? extends ByteBuffer> largePacketSender, int maxPieceSize) {
		this.largePacketSender = largePacketSender;
		this.maxPieceSize = maxPieceSize;
	}
	

	@Override 
	synchronized
	public ByteBuffer produce() {
		ByteBuffer nextPiece = nextPiece();
		return nextPiece == null ? firstPiece() : nextPiece;
	}
	
	
	private ByteBuffer firstPiece() {
		whole = largePacketSender.produce();
			
		int numberOfPieces = (whole.remaining() + BYTES_FOR_REMAINING_PIECES - 1) / maxPieceSize + 1;
		if (numberOfPieces > MAX_PIECES) throw new IllegalArgumentException("Packet split in " + numberOfPieces + " pieces. Max number of pieces: " + MAX_PIECES);
			
		bytesPerPiece = (whole.remaining() + BYTES_FOR_REMAINING_PIECES - 1) / numberOfPieces + 1;
			
		byte[] piece = new byte[bytesPerPiece];
		piece[0] = (byte) (numberOfPieces - 1);
		whole.get(piece, BYTES_FOR_REMAINING_PIECES, bytesPerPiece - BYTES_FOR_REMAINING_PIECES);
		
		return ByteBuffer.wrap(piece);
	}


	private ByteBuffer nextPiece() {
		if (whole.remaining() == 0) return null;
		
		int size = min(whole.remaining(), bytesPerPiece);
		byte[] piece = new byte[size];
		whole.get(piece);
		
		return ByteBuffer.wrap(piece);
	}
}