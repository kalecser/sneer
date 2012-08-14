package sneer.bricks.network.computers.udp.packet.impl;

import static basis.environments.Environments.my;
import static java.lang.Math.min;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import sneer.bricks.network.computers.udp.packet.PacketSplitter;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.notifiers.Source;

class PacketSplitterImpl implements PacketSplitter {
	
	private static final int BYTES_FOR_REMAINING_PIECES = 1;
	
	private final Notifier<ByteBuffer> lastPacketJoined = my(Notifiers.class).newInstance();
	private final ArrayList<ByteBuffer> piecesToJoin = new ArrayList<>();
	private int piecesRemaining = 0;
	
	@Override
	public ByteBuffer[] split(ByteBuffer whole, int maxPieceSize) {
		int numberOfPieces = (whole.remaining() + BYTES_FOR_REMAINING_PIECES - 1) / maxPieceSize + 1;
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
	public void join(ByteBuffer piece) {
		if (!lastPiece(piece)) return;
		joinPieces();
		piecesToJoin.clear();
	}


	private boolean lastPiece(ByteBuffer piece) {
		accumulatePiece(piece);
		return piecesRemaining == 0;
	}


	private void accumulatePiece(ByteBuffer piece) {
		if (piecesToJoin.isEmpty()) {
			piecesRemaining = piece.get();
			piecesToJoin.ensureCapacity(piecesRemaining + 1); 
			piecesToJoin.add(piece.slice());
			return;
		}
		
		piecesToJoin.add(piece);
		piecesRemaining--;
	}


	private void joinPieces() {
		ByteBuffer ret = ByteBuffer.allocate(packetSize());
		for (ByteBuffer piece : piecesToJoin)
			ret.put(piece);
		
		ret.flip();
		lastPacketJoined.notifyReceivers(ret);
	}


	private int packetSize() {
		int ret = 0;
		for (ByteBuffer piece : piecesToJoin)
			ret += piece.remaining();
		
		return ret;
	}


	@Override
	public Source<ByteBuffer> lastJoinedPacket() {
		return lastPacketJoined.output();
	}

}
