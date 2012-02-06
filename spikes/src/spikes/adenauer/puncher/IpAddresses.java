package spikes.adenauer.puncher;

import java.net.InetSocketAddress;


class IpAddresses {

	final InetSocketAddress publicInternetAddress;
	final InetSocketAddress localNetworkAddress;

	
	public IpAddresses(InetSocketAddress localNetworkAddr, InetSocketAddress publicInternetAddr) {
		this.localNetworkAddress = localNetworkAddr;
		this.publicInternetAddress = publicInternetAddr;
	}

}
