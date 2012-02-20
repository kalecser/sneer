package sneer.bricks.pulp.notifiers.pulsers.impl;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.pulsers.PulseSender;
import sneer.bricks.pulp.notifiers.pulsers.PulseSenders;
import sneer.bricks.pulp.notifiers.pulsers.Pulser;

class PulseSendersImpl implements PulseSenders {

	@Override
	public PulseSender newInstance() {
		return new PulserSenderImpl();
	}

	@Override
	public WeakContract receive(Runnable receiver, Pulser... multipleSources) {
		return new UmbrellaContract(receiver, multipleSources);
	}

}