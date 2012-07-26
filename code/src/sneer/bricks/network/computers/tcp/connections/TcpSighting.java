package sneer.bricks.network.computers.tcp.connections;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class TcpSighting extends Tuple {

	public final Seal peersSeal;
	public final String ip;

	public TcpSighting(Seal seal_, String ip_){
		peersSeal = seal_;
		ip = ip_;
	}
	
	@Override
	public String toString() {
		return "Peer Sighting seal: " + peersSeal + " ip: " + ip;
	}
	
}
