package spikes.adenauer.puncher;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;



class SocketAddressUtils {

	static final Charset CHARSET = Charset.forName("UTF-8");
	
	static boolean isSameHost(InetSocketAddress a1, InetSocketAddress a2) {
		return a1.getHostName().equals(a2.getHostName());
	}

	static byte[] marshal(InetSocketAddress addr) {
		return (addr.getHostName() + ":" + addr.getPort()).getBytes(CHARSET);
	}

	
	static InetSocketAddress unmarshal(String address) throws InvalidAddress {
		String[] parts = address.split(":");
		try {
			String host = parts[0];
			int port = Integer.parseInt(parts[1]);
			return new InetSocketAddress(host, port);
		} catch (RuntimeException e) {
			throw new InvalidAddress("Unable to parse address: " + address, e);
		}
	}

}
