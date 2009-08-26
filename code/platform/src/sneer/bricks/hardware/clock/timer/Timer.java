package sneer.bricks.hardware.clock.timer;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.brickness.Brick;

@Brick
public interface Timer {

	void sleepAtLeast(long millis);
	WeakContract wakeUpNoEarlierThan(long timeToWakeUp, Runnable runnable);
	WeakContract wakeUpInAtLeast(long millisFromNow, Runnable runnable);
	WeakContract wakeUpEvery(long minimumPeriodInMillis, Runnable stepper);
	WeakContract wakeUpNowAndEvery(long minimumPeriodInMillis, Runnable stepper);

}
