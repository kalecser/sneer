package sneer.bricks.network.computers.udp.sightings;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class UdpSighting extends Tuple {

	public final Seal peerSeal;
	public final String host;
	public final int port;
	
	public UdpSighting(Seal peerSeal, String host, int port) {
		this.peerSeal = peerSeal;
		this.host = host;
		this.port = port;		
	}

}
