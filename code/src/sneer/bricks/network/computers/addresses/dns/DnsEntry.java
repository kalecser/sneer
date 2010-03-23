package sneer.bricks.network.computers.addresses.dns;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.tuples.Tuple;

public class DnsEntry extends Tuple {

	public final Seal _seal;
	public final String _ip;

	public DnsEntry(Seal seal, String ip){
		_seal = seal;
		_ip = ip;
	}
	
	@Override
	public String toString() {
		return "Dns entry seal: " + _seal + " ip: " + _ip;
	}
	
}
