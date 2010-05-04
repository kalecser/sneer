package sneer.bricks.pulp.reactive.gates.logic.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.gates.logic.LogicGates;
import sneer.foundation.lang.Functor;

public class LogicGatesImpl implements LogicGates {

	@Override
	public Signal<Boolean> not(Signal<Boolean> signal) {
		return my(Signals.class).adapt(signal, new Functor<Boolean, Boolean>() { @Override public Boolean evaluate(Boolean value) {
			return !value;
		}});
	}

	@Override
	public Signal<Boolean> and(Signal<Boolean> a, Signal<Boolean> b) {
		return new And(a, b).output();
	}

}
