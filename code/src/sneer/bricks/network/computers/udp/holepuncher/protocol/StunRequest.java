package sneer.bricks.network.computers.udp.holepuncher.protocol;

import java.net.InetAddress;

import basis.lang.Immutable;



public class StunRequest extends Immutable {

	public final byte[] _ownSeal;
	public final byte[] _peerToFind;
	public final InetAddress _localIp;
	public final int _localPort;

	
	public StunRequest(byte[] seal, InetAddress localIp, int localPort, byte[] peerToFind) {
		_ownSeal = seal;
		_localIp = localIp;
		_localPort = localPort;
		_peerToFind = peerToFind;
	}

}
