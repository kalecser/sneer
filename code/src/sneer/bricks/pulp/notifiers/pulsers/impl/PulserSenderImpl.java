package sneer.bricks.pulp.notifiers.pulsers.impl;

import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.notifiers.pulsers.PulseSender;
import sneer.bricks.pulp.notifiers.pulsers.Pulser;
import static basis.environments.Environments.my;

class PulserSenderImpl implements PulseSender {

	private final Notifier<Object> _delegate = my(Notifiers.class).newInstance();

	@Override
	public Pulser output() {
		return _delegate.output();
	}

	@Override
	public void sendPulse() {
		_delegate.notifyReceivers(null);
	}

}
