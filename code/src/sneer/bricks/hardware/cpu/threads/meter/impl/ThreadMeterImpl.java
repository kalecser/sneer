package sneer.bricks.hardware.cpu.threads.meter.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.meter.ThreadMeter;
import sneer.bricks.hardware.io.log.Logger;

class ThreadMeterImpl implements ThreadMeter {

	private int _threadCountPeak;
	
	@SuppressWarnings("unused") private WeakContract _refToAvoidGc;

	
	{
		_refToAvoidGc = my(Timer.class).wakeUpEvery(5000, new Runnable() { @Override public void run() {
			logIfPeak();
		}});
	}

	
	private void logIfPeak() {
		int count = Thread.getAllStackTraces().size();
		if (count <= _threadCountPeak) return;
		_threadCountPeak = count;
		my(Logger.class).log("Thread peak reached: {} threads.", _threadCountPeak);
	}
	
}
