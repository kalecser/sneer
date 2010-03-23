package sneer.bricks.network.computers.addresses.sighting;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.tuples.Tuple;

public class Sighting extends Tuple {

	public final Seal _peersSeal;
	public final String _ip;

	public Sighting(Seal seal, String ip){
		_peersSeal = seal;
		_ip = ip;
	}
	
	@Override
	public String toString() {
		return "Dns entry seal: " + _peersSeal + " ip: " + _ip;
	}
	
}
