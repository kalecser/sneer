package sneer.bricks.snapps.games.go.impl.sneerSpecifics;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.games.go.impl.TimerFactory;

public final class SneerTimerFactory implements TimerFactory {

	@Override
	public WeakContract wakeUpEvery(int interval, Runnable runnable) {
		return my(Timer.class).wakeUpEvery(interval, runnable);	 
	}
} 