package sneer.bricks.network.computers.udp.holepuncher.protocol;

import basis.lang.Immutable;

public class StunRequest extends Immutable {

	public final byte[] ownSeal;
	public final byte[][] peerSealsToFind;
	public final byte[] localAddressData;
	
	public StunRequest(byte[] seal_, byte[][] peerSealsToFind_, byte[] localAddressData_) {
		ownSeal = seal_;
		peerSealsToFind = peerSealsToFind_;
		localAddressData = localAddressData_;
	}

}
