package sneer.bricks.network.computers.udp.holepuncher.protocol;

import java.net.InetAddress;

import basis.lang.Immutable;


public class StunReply extends Immutable {

	public final byte[] peerSeal;
	public final InetAddress peerIp;
	public final int peerPort;
	public final InetAddress peerLocalIp;
	public final int peerLocalPort;
	
	
	public StunReply(byte[] peerSeal_, InetAddress peerIp_, int peerPort_, InetAddress peerLocalIp_, int peerLocalPort_) {
		peerSeal = peerSeal_;
		peerIp = peerIp_;
		peerPort = peerPort_;
		peerLocalIp = peerLocalIp_;
		peerLocalPort = peerLocalPort_;
	}

}