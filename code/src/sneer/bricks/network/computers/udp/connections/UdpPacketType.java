package sneer.bricks.network.computers.udp.connections;


public enum UdpPacketType {
	
	Hail, Data, Stun, Handshake;
	
	static public UdpPacketType search(int ordinal) {
		if (ordinal < 0) return null;
		if (ordinal >= values().length) return null;
		return UdpPacketType.values()[ordinal];
	}
}