package sneer.bricks.pulp.reactive.gates.strings.impl;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.gates.strings.StringGates;

class StringGatesImpl implements StringGates {

	@Override
	public Signal<String> concat(Signal<String>... strings) {
		return new Concatenator(strings).output();
	}

	@Override
	public Signal<String> concat(String separator, Signal<String>... strings) {
		return new Concatenator(separator, strings).output();
	}

}
