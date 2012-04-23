package sneer.bricks.network.computers.tcp;

import java.io.IOException;

public interface ByteArraySocket {
	
	static final int MAX_ARRAY_SIZE = 1024 * 20;

	byte[] read() throws IOException;

	void write(byte[] array) throws IOException;
	
	void close();

}
