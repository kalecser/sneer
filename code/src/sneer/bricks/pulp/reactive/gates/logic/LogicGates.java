package sneer.bricks.pulp.reactive.gates.logic;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface LogicGates {

	Signal<Boolean> not(Signal<Boolean> b);

	Signal<Boolean> and(Signal<Boolean> a, Signal<Boolean> b);

}
