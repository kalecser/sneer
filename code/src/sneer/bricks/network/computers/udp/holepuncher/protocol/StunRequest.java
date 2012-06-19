package sneer.bricks.network.computers.udp.holepuncher.protocol;


public class StunRequest {

	public final byte[] ownSeal;
	public final byte[][] peerSealsToFind;
	public final byte[] localAddressData;
	
	public StunRequest(byte[] seal_, byte[][] peerSealsToFind_, byte[] localAddressData_) {
		ownSeal = seal_;
		peerSealsToFind = peerSealsToFind_;
		localAddressData = localAddressData_;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " peerSealsToFind: " + peerSealsToFind.length + ", localAddressData: " + localAddressData.length + " bytes.";
	}
	
	

}
