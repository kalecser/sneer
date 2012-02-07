package spikes.adenauer.puncher;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;



class SocketAddressUtils {

	static final Charset CHARSET = Charset.forName("UTF-8");
	
	
	static byte[] marshal(IpAddresses addresses) {
		InetSocketAddress pub = addresses.publicInternetAddress;
		InetSocketAddress local = addresses.localNetworkAddress;
		return (asString(pub) + "," + asString(local)).getBytes(CHARSET);
	}
	
	
	static IpAddresses unmarshalAddresses(String string) throws UnableToParseAddress {
		String[] parts = string.split(",");
		if (parts.length != 2)
			throw new UnableToParseAddress("Addresses should have two parts: " + string);
		String pub = parts[0];
		String local = parts[1];
		return new IpAddresses(unmarshal(pub), unmarshal(local));
	}
	

	private static String asString(InetSocketAddress addr) {
		return ip(addr) + ":" + addr.getPort();
	}

	
	static InetSocketAddress unmarshal(String address) throws UnableToParseAddress {
		String[] parts = address.split(":");
		try {
			String host = parts[0];
			int port = Integer.parseInt(parts[1]);
			return new InetSocketAddress(host, port);
		} catch (RuntimeException e) {
			throw new UnableToParseAddress("Unable to parse address: " + address, e);
		}
	}
	
	
	private static String ip(InetSocketAddress addr) {
		return addr.getAddress().getHostAddress();
	}

}
