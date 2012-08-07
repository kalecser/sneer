package sneer.bricks.network.computers.udp.packet.impl;

import static java.lang.Math.min;

import java.nio.ByteBuffer;

import sneer.bricks.network.computers.udp.packet.PacketSplitter;

class PacketSplitterImpl implements PacketSplitter {

	@Override
	public ByteBuffer[] splitBy(ByteBuffer whole, int pieceSize) {
		int numberOfBuffers = ((whole.remaining() - 1) / pieceSize) + 1;
		int bytesPerPiece = ((whole.remaining() - 1) / numberOfBuffers) + 1;
		
		ByteBuffer[] ret = new ByteBuffer[numberOfBuffers];
		
		for (int i = 0; i < numberOfBuffers; i++) {
			int destSize = min(whole.remaining(), bytesPerPiece);
			byte[] dest = new byte[destSize];
			whole.get(dest);
			
			ret[i] = ByteBuffer.wrap(dest);
		}
		
		return ret;
	}
	
	
	@Override
	public ByteBuffer join(ByteBuffer[] pieces) {
		int size = 0;
		for (ByteBuffer peice : pieces)
			size += peice.remaining();
		
		ByteBuffer ret = ByteBuffer.allocate(size);
		
		for (ByteBuffer piece : pieces)
			ret.put(piece);
		
		ret.flip();
		
		return ret;
	}

}
