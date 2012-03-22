package sneer.bricks.pulp.reactive.gates.logic.impl;

import static basis.environments.Environments.my;
import basis.lang.Closure;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.notifiers.pulsers.PulseSenders;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class And {

	private final Register<Boolean> _result = my(Signals.class).newRegister(false);
	private final Signal<Boolean> _a;
	private final Signal<Boolean> _b;

	@SuppressWarnings("unused") private final WeakContract _referenceToAvoidGc;

	public And(Signal<Boolean> a, Signal<Boolean> b) {
		_a = a;
		_b = b;

		_referenceToAvoidGc = my(PulseSenders.class).receive(new Closure(){@Override public void run() {
			refresh();
		}}, a, b);
	}

	private void refresh() {
		_result.setter().consume(_a.currentValue() && _b.currentValue());
	}

	public Signal<Boolean> output() {
		return my(WeakReferenceKeeper.class).keep(_result.output(), this);
	}
}