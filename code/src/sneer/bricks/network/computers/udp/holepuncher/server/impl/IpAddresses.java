package sneer.bricks.network.computers.udp.holepuncher.server.impl;

import java.net.InetAddress;


public class IpAddresses {

	public final InetAddress publicInternetAddress;
	public final int publicInternetPort;
	public byte[] localAddressData;


	public IpAddresses(InetAddress publicInternetAddr_, int publicInternetPort_, byte[] localAddressData_) {
		publicInternetAddress = publicInternetAddr_;
		publicInternetPort = publicInternetPort_;
		localAddressData = localAddressData_;		
	}

}
