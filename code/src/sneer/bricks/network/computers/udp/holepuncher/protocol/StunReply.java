package sneer.bricks.network.computers.udp.holepuncher.protocol;

import java.net.InetAddress;


public class StunReply {

	public final byte[] peerSeal;
	public final InetAddress peerIp;
	public final int peerPort;
	public final byte[] peerLocalAddressData;
	
	
	public StunReply(byte[] peerSeal_, InetAddress peerIp_, int peerPort_, byte[] peerLocalAddressData_) {
		peerSeal = peerSeal_;
		peerIp = peerIp_;
		peerPort = peerPort_;
		peerLocalAddressData = peerLocalAddressData_;		
	}

}