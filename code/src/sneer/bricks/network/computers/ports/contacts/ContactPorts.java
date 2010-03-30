package sneer.bricks.network.computers.ports.contacts;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.reactive.Signal;


public interface ContactPorts {

	Signal<Integer> portGiven(Seal seal);

}
