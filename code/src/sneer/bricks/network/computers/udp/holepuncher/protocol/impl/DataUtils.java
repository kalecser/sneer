package sneer.bricks.network.computers.udp.holepuncher.protocol.impl;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class DataUtils {

	public static DataInputStream dataInputFrom(byte[] data, int length) {
		ByteArrayInputStream ret = new ByteArrayInputStream(data, 0, length);
		return new DataInputStream(ret);
	}

	static byte[] readNextArray(ByteBuffer in, int length) {
		if (!in.hasRemaining()) return null;
		byte[] ret = new byte[length];
		in.get(ret);
		return ret;
	}

	public static InetAddress ip(byte[] bytes) {
		try {
			return InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

}
