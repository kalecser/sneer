package sneer.bricks.network.computers.channels.guaranteed.splitter.impl;

import static basis.environments.Environments.my;
import static java.lang.Math.min;

import java.nio.ByteBuffer;

import sneer.bricks.network.computers.channels.guaranteed.splitter.PacketSplitter;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.notifiers.Source;

class PacketSplitterImpl implements PacketSplitter {
	
	private static final int BYTES_FOR_REMAINING_PIECES = 1; //Representing up to PacketSplitter.MAX_PIECES
	
	private final Notifier<ByteBuffer> lastPacketJoined = my(Notifiers.class).newInstance();
	private final int maxPieceSize;
	private ByteBuffer joining;
	private int piecesMissing = 0;
	
	
	PacketSplitterImpl(int maxPieceSize) {
		this.maxPieceSize = maxPieceSize;
	}


	@Override
	public ByteBuffer[] split(ByteBuffer whole) {
		int numberOfPieces = (whole.remaining() + BYTES_FOR_REMAINING_PIECES - 1) / maxPieceSize + 1;
		if (numberOfPieces > MAX_PIECES)
			throw new IllegalArgumentException("Packet split in " + numberOfPieces + " pieces. Max number of pieces: " + MAX_PIECES);
		
		int bytesPerPiece = (whole.remaining() + BYTES_FOR_REMAINING_PIECES - 1) / numberOfPieces + 1;
		
		ByteBuffer[] ret = new ByteBuffer[numberOfPieces];
		ret[0] = firstPiece(whole, numberOfPieces, bytesPerPiece);

		for (int i = 1; i < numberOfPieces; i++)
			ret[i] = piece(whole, bytesPerPiece);
		
		return ret;
	}


	private ByteBuffer firstPiece(ByteBuffer whole, int numberOfPieces, int bytesPerPiece) {
		byte[] piece = new byte[bytesPerPiece];
		piece[0] = (byte) (numberOfPieces - 1);
		whole.get(piece, BYTES_FOR_REMAINING_PIECES, bytesPerPiece - BYTES_FOR_REMAINING_PIECES);

		return ByteBuffer.wrap(piece);
	}


	private ByteBuffer piece(ByteBuffer whole, int bytesPerPiece) {
		int size = min(whole.remaining(), bytesPerPiece);
		byte[] piece = new byte[size];
		whole.get(piece);
		
		return ByteBuffer.wrap(piece);
	}


	@Override
	synchronized
	public void join(ByteBuffer piece) {
		accumulatePiece(piece);
		if (piecesMissing > 0) return;
		
		joining.flip();
		lastPacketJoined.notifyReceivers(joining);
		joining = null;
	}


	private void accumulatePiece(ByteBuffer piece) {
		if (joining == null) {
			int pieceSize = piece.remaining();
			piecesMissing = piece.get() & 0xFF;
			joining = ByteBuffer.allocate((1 + piecesMissing) * pieceSize - BYTES_FOR_REMAINING_PIECES);
		} else {
			piecesMissing--;
		}
		
		joining.put(piece);
	}


	@Override
	public Source<ByteBuffer> lastJoinedPacket() {
		return lastPacketJoined.output();
	}

}
