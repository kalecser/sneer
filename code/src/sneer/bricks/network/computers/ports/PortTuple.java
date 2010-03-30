package sneer.bricks.network.computers.ports;

import sneer.bricks.pulp.tuples.Tuple;

public class PortTuple extends Tuple {

	public final int port;

	public PortTuple(int port_) {
		port = port_;
	}

}
