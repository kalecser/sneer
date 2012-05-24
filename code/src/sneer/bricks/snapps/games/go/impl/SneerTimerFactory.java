package sneer.bricks.snapps.games.go.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.games.go.impl.gui.TimerFactory;

final class SneerTimerFactory implements TimerFactory {

	@Override
	public WeakContract wakeUpEvery(int interval, Runnable runnable) {
		return my(Timer.class).wakeUpEvery(interval, runnable);	 
	}
}