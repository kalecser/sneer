package sneer.bricks.pulp.reactive.gates.strings.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Arrays;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.events.pulsers.Pulsers;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Closure;

class Concatenator {

	private final List<Signal<String>> _chunks;
	private final String _separator;

	private final Register<String> _concat = my(Signals.class).newRegister("");

	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;

	Concatenator(Signal<String>... chunks) {
		this(null, chunks);
	}

	Concatenator(String separator, Signal<String>... chunks) {
		_separator = separator;
		_chunks = Arrays.asList(chunks);

		_refToAvoidGc = my(Pulsers.class).receive(new Closure() { @Override public void run() {
			refresh();
		}}, chunks);
	}

	private void refresh() {
		String whole = my(Lang.class).strings().join(_chunks, _separator);
		_concat.setter().consume(my(Lang.class).strings().strip(whole, _separator));
	}

	Signal<String> output() {
		return my(WeakReferenceKeeper.class).keep(_concat.output(), this);
	}

}
