package sneer.bricks.network.computers.udp.holepuncher.protocol;

import basis.lang.Immutable;

public class StunRequest extends Immutable {

	public final byte[] _ownSeal;
	public final byte[] _peerToFind;
	public final byte[] _localAddressData;
	
	public StunRequest(byte[] seal, byte[] peerToFind, byte[] localAddressData) {
		_ownSeal = seal;
		_peerToFind = peerToFind;
		_localAddressData = localAddressData;
	}

}
