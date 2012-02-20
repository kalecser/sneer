// Heartbeat (tuple) "Im alive at this IP and port" flooded every 15 seconds
//		OwnName
//		OwnLocalIp
//		OwnPort
//
// PeerLookup (tuple) "Where is this peer?" flooded every 15 seconds for offline peers that are issueing heartbeats
//		PeerSeal
//
// PeerSighting (tuple) "I have seen this peer on this IP and Port" sent to whoever did a lookup.
//		Seal (peer)
//		IP, port
//


package spikes.adenauer.network;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.notifiers.Source;
import sneer.bricks.pulp.reactive.collections.SetSignal;

public interface UdpNetworkSpike {
	
	static final int MAX_ARRAY_SIZE = 1024 * 20;


	SetSignal<Seal> peersOnline();
	void send(byte[] data, Seal destination);
	Source<Packet> packetsReceived();


	public interface Packet {
		Seal sender();
		byte[] data();
	}

}
