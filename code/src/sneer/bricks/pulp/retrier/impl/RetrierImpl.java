package sneer.bricks.pulp.retrier.impl;

import static basis.environments.Environments.my;
import basis.lang.Closure;
import basis.lang.exceptions.FriendlyException;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.exceptions.Hiccup;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.retrier.Retrier;
import sneer.bricks.pulp.retrier.Task;

class RetrierImpl implements Retrier {

	private final Threads _threads = my(Threads.class);
	private final BlinkingLights _lights = my(BlinkingLights.class);
	
	private volatile boolean _isStillTrying = true;
	private final Light _light = _lights.prepare(LightType.ERROR);
	private final Contract _steppingContract;
	
	RetrierImpl(final int periodBetweenAttempts, final Task task) {
		_steppingContract = _threads.startStepping(new Closure() { @Override public void run() {
			if (wasSuccessful(task))
				_steppingContract.dispose();
			else
				my(Timer.class).sleepAtLeast(periodBetweenAttempts);
		}});
	}

	
	@Override
	synchronized public void giveUpIfStillTrying() {
		_isStillTrying = false;
		turnLightOff();
	}

	
	private void turnLightOff() {
		_lights.turnOffIfNecessary(_light);
	}


	private boolean wasSuccessful(final Task task) {
		try {
			if (_isStillTrying) task.execute();
		} catch (Hiccup ok) {
		} catch (FriendlyException fx) {
			dealWith(fx);
			return false;
		}
		
		turnLightOff();
		return true;
	}

	synchronized private void dealWith(FriendlyException fx) {
		if (!_isStillTrying) return;
		_lights.turnOnIfNecessary(_light, fx);
	}

}
