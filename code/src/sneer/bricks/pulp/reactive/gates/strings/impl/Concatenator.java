package sneer.bricks.pulp.reactive.gates.strings.impl;

import static basis.environments.Environments.my;

import java.util.Arrays;
import java.util.List;

import basis.lang.Closure;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.notifiers.pulsers.PulseSenders;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class Concatenator {

	private final List<Signal<?>> _chunks;
	private final String _separator;

	private final Register<String> _concat = my(Signals.class).newRegister("");

	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;

	Concatenator(Signal<?>... chunks) {
		this("", chunks);
	}

	Concatenator(String separator, Signal<?>... chunks) {
		_separator = separator;
		_chunks = Arrays.asList(chunks);

		_refToAvoidGc = my(PulseSenders.class).receive(new Closure() { @Override public void run() {
			refresh();
		}}, chunks);
	}

	private void refresh() {
		_concat.setter().consume(
			my(Lang.class).strings().chomp(concatenation(), _separator)
		);
	}

	private String concatenation() {
		StringBuilder result = new StringBuilder();
		for (Signal<?> chunk : _chunks) {
			Object chunkValue = chunk.currentValue();
			if (chunkValue != null && chunkValue.toString().isEmpty()) continue;
			result.append(chunkValue).append(_separator);
		}
		return result.toString();
	}

	Signal<String> output() {
		return my(WeakReferenceKeeper.class).keep(_concat.output(), this);
	}

}
