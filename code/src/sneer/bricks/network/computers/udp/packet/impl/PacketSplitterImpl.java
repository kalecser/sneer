package sneer.bricks.network.computers.udp.packet.impl;

import static basis.environments.Environments.my;
import static java.lang.Math.min;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.network.computers.udp.packet.PacketSplitter;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.notifiers.Source;

class PacketSplitterImpl implements PacketSplitter {
	
	private final Notifier<ByteBuffer> lastPacketJoined = my(Notifiers.class).newInstance();
	private final List<ByteBuffer> piecesToJoin = new ArrayList<>();
	private int piecesRemaining = 0;
	
	@Override
	public ByteBuffer[] split(ByteBuffer whole, int maxPieceSize) {
		int numberOfBuffers = (whole.remaining() / maxPieceSize) + 1;
		int bytesPerPiece = (whole.remaining() / numberOfBuffers) + 1;
		
		ByteBuffer[] ret = new ByteBuffer[numberOfBuffers];
		ret[0] = firstPiece(whole, numberOfBuffers, bytesPerPiece);

		for (int i = 1; i < numberOfBuffers; i++)
			ret[i] = piece(whole, bytesPerPiece);
		
		return ret;
	}


	private ByteBuffer firstPiece(ByteBuffer whole, int numberOfPieces, int bytesPerPiece) {
		int size = min(whole.remaining() + 1, bytesPerPiece);
		byte[] piece = new byte[size];
		piece[0] = (byte) (numberOfPieces - 1);
		whole.get(piece, 1, size - 1);
		
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
