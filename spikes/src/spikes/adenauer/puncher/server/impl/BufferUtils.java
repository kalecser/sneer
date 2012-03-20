package spikes.adenauer.puncher.server.impl;



public class BufferUtils {

	static int append(byte[] buf, int pos, byte[] content) {
		System.arraycopy(content, 0, buf, pos, content.length);
		pos += content.length;
		return pos;
	}

}
