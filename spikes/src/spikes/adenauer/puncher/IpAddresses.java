package spikes.adenauer.puncher;

import java.net.InetSocketAddress;


class IpAddresses {

	final InetSocketAddress publicInternetAddress;
	final InetSocketAddress localNetworkAddress;

	
	public IpAddresses(InetSocketAddress publicInternetAddr, InetSocketAddress localNetworkAddr) {
		this.publicInternetAddress = publicInternetAddr;
		this.localNetworkAddress = localNetworkAddr;
	}

}
