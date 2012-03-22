package sneer.bricks.pulp.reactive.gates.numbers;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface NumberGates {

	Signal<Integer> add(Signal<Integer> a, Signal<Integer> b);
	
}
