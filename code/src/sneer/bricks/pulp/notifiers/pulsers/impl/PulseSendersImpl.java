package sneer.bricks.pulp.notifiers.pulsers.impl;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.pulsers.PulseSender;
import sneer.bricks.pulp.notifiers.pulsers.PulseSenders;
import sneer.bricks.pulp.notifiers.pulsers.Pulser;
import basis.lang.Closure;

class PulseSendersImpl implements PulseSenders {

	@Override
	public PulseSender newInstance() {
		return new PulserSenderImpl();
	}

	@Override
	public WeakContract receive(Closure receiver, Pulser... multipleSources) {
		return new UmbrellaContract(receiver, multipleSources);
	}

}