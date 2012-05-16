package sneer.bricks.network.computers.udp.holepuncher.impl;

import java.net.InetAddress;


public class IpAddresses {

	public final InetAddress publicInternetAddress;
	public final int publicInternetPort;
	public final InetAddress localNetworkAddress;
	public final int localNetworkPort;


	public IpAddresses(InetAddress publicInternetAddr, int publicInternetPort_, InetAddress localNetworkAddr, int localNetworkPort_) {
		this.publicInternetAddress = publicInternetAddr;
		this.publicInternetPort = publicInternetPort_;
		this.localNetworkAddress = localNetworkAddr;
		this.localNetworkPort = localNetworkPort_;
	}

}
