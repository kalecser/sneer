package sneer.bricks.pulp.reactive.gates.strings;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface StringGates {

	Signal<String> concat(Signal<?>... objects);

	Signal<String> concat(String separator, Signal<?>... objects);

}
