package spikes.sandro.summit.register.logger.impl;

import static basis.environments.Environments.my;
import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.Signal;
import spikes.sandro.summit.register.SimpleRegister;
import spikes.sandro.summit.register.logger.SimpleLogger;

class SimpleLoggerImpl implements SimpleLogger {

	@SuppressWarnings("unused") private final Object _referenceToAvoidGc;

	SimpleLoggerImpl() {
		Consumer<String> receiver = new Consumer<String>() {
			@Override public void consume(String value) {
				System.out.println(value);
			}
		};

		Signal<String> output = my(SimpleRegister.class).output();
		_referenceToAvoidGc = output.addReceiver(receiver);
	}
}
