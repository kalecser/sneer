package sneer.bricks.network.computers.udp.packet.impl;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

import java.nio.ByteBuffer;
import sneer.bricks.network.computers.udp.packet.PacketSplitter;

class PacketSplitterImpl implements PacketSplitter {

	@Override
	public ByteBuffer[] splitBy(int size, ByteBuffer packet) {
		int numberOfBuffers = (int) ceil(packet.capacity() / (double)size);
		
		ByteBuffer[] ret = new ByteBuffer[numberOfBuffers];
		
		for (int i = 0; i < numberOfBuffers; i++) {
			int destSize = min(packet.remaining(), size);
			byte[] dest = new byte[destSize];
			packet.get(dest);
			
			ret[i] = ByteBuffer.wrap(dest);
		}
		
		return ret;
	}

}
