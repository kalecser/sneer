package sneer.bricks.network.computers.channels;

import java.nio.ByteBuffer;

import sneer.bricks.hardware.cpu.crypto.Hash;
import basis.lang.Consumer;
import basis.lang.Producer;


public interface Channel {
	
	Hash id();
	
	void open(Producer<ByteBuffer> sender, Consumer<ByteBuffer> receiver);
	
	int maxPacketSize();

}
