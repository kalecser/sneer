package sneer.bricks.pulp.reactive.gates.strings.impl;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.gates.strings.StringGates;

class StringGatesImpl implements StringGates {

	@Override
	public Signal<String> concat(Signal<?>... objects) {
		return new Concatenator(objects).output();
	}

	@Override
	public Signal<String> concat(String separator, Signal<?>... objects) {
		return new Concatenator(separator, objects).output();
	}

}
