package sneer.bricks.network.computers.ports.contacts;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface ContactPorts {

	Signal<Integer> portGiven(Seal seal);

}
