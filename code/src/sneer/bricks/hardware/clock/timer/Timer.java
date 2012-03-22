package sneer.bricks.hardware.clock.timer;

import basis.brickness.Brick;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;

@Brick
public interface Timer {

	void sleepAtLeast(long millis);
	WeakContract wakeUpNoEarlierThan(long timeToWakeUp, Runnable runnable);
	WeakContract wakeUpInAtLeast(long millisFromNow, Runnable runnable);
	WeakContract wakeUpEvery(long minimumPeriodInMillis, Runnable stepper);
	WeakContract wakeUpNowAndEvery(long minimumPeriodInMillis, Runnable stepper);

}
