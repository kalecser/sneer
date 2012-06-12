package sneer.bricks.network.computers.udp.holepuncher.protocol.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


class DataUtils {

	static byte[] getNextArray(ByteBuffer in, int length) {
		if (!in.hasRemaining()) return null;
		byte[] ret = new byte[length];
		in.get(ret);
		return ret;
	}

	static InetAddress ip(byte[] bytes) {
		if(bytes == null) return null;
		try {
			return InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

}
