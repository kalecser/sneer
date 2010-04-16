package sneer.bricks.network.computers.ports;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.PickyConsumer;

@Brick
public interface OwnPort {

	Signal<Integer> port();

	PickyConsumer<Integer> portSetter();

}
