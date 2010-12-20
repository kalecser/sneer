package sneer.bricks.network.social.heartbeat.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.heartbeat.Heart;
import sneer.bricks.network.social.heartbeat.Heartbeat;
import sneer.foundation.lang.Closure;

class HeartImpl implements Heart {
	
	@SuppressWarnings("unused")
	private final WeakContract _timerContract;
	
	{
		_timerContract = my(Timer.class).wakeUpNowAndEvery(10 * 1000, new Closure() { @Override public void run() {
			beat();
		}});
	}

	private void beat() {
		my(TupleSpace.class).add(new Heartbeat());
	}
	
}
