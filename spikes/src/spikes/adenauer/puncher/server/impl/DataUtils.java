package spikes.adenauer.puncher.server.impl;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class DataUtils {

	public static DataInputStream dataInputFrom(byte[] data, int length) {
		ByteArrayInputStream ret = new ByteArrayInputStream(data, 0, length);
		return new DataInputStream(ret);
	}

	public static byte[] readNewArray(DataInputStream in, int length) throws IOException {
		byte[] ret = new byte[length];
		int count = in.read(ret);
		if (count == -1) return null;
		if (count != length) throw new IOException("Trying to read "+length+" bytes but only "+count+" were available.");
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
