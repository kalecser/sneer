package sneer.bricks.hardware.clock.timer.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.SortedSet;
import java.util.TreeSet;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.Contracts;
import sneer.bricks.hardware.cpu.lang.contracts.Disposable;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.Consumer;

class TimerImpl implements Timer {
	
	private static final Alarm[] ALARM_ARRAY_TYPE = new Alarm[0];
	private final Clock _clock = my(Clock.class);
	private final SortedSet<Alarm> _alarms = new TreeSet<Alarm>();
	
	@SuppressWarnings("unused") private final WeakContract _timeContract;
	
	
	TimerImpl(){
		_timeContract = _clock.time().addReceiver(new Consumer<Long>(){ @Override public void consume(Long value) {
			wakeUpAlarmsIfNecessary();
		}});
	}
	
	
	@Override
	synchronized
	public WeakContract wakeUpNoEarlierThan(long timeToWakeUp, Runnable runnable) {
		long millisFromNow = timeToWakeUp <= currentTime()
			? 0
			: timeToWakeUp - currentTime();
		return wakeUpInAtLeast(millisFromNow, runnable);
	}

	@Override
	synchronized
	public WeakContract wakeUpInAtLeast(long millisFromCurrentTime, Runnable runnable) {
		return wakeUp(millisFromCurrentTime, runnable, false);
	}

	@Override
	synchronized
	public WeakContract wakeUpNowAndEvery(long period, final Runnable stepper) {
		Alarm alarm = new Alarm(stepper, period, true);
		alarm.wakeUp();
		return my(Contracts.class).weakContractFor(alarm);
	}

	@Override
	synchronized
	public WeakContract wakeUpEvery(long period, Runnable stepper) {
		return wakeUp(period, stepper, true);
	}

	private WeakContract wakeUp(long period, Runnable stepper, boolean isPeriodic) {
		Alarm alarm = new Alarm(stepper, period, isPeriodic);
		_alarms.add(alarm);
		return my(Contracts.class).weakContractFor(alarm);
	}

	@Override
	public void sleepAtLeast(long millis) {
		Latch latch = my(Latches.class).produce();
		wakeUpInAtLeast(millis, latch);
		latch.waitTillOpen();
	}
	
	synchronized
	private void wakeUpAlarmsIfNecessary() {
		for (Alarm alarm : _alarms.toArray(ALARM_ARRAY_TYPE)) {
			if (!alarm.isTimeToWakeUp()) return;
			alarm.wakeUp();
		}
	}

	
	static private long _nextSequence = 0;

	private class Alarm implements Comparable<Alarm>, Disposable {

		private final Runnable _stepper;

		private final boolean _isPeriodic;
		private final long _period;
		private long _wakeUpTime;
		private final long _sequence = _nextSequence++;

		volatile
		private boolean _isRunning;

		volatile
		private boolean _isDisposed = false;

		
		public Alarm(Runnable stepper, long period, boolean isPeriodic) {
			if (period < 0) throw new IllegalArgumentException("" + period);
			_stepper = stepper;
			_period = period;
			_wakeUpTime = currentTime() + period;
			_isPeriodic = isPeriodic;
		}

		
		void wakeUp() {
			_alarms.remove(this);
			if (_isDisposed) return;
			
			step(_stepper);

			if (!_isPeriodic) return;
			_wakeUpTime = currentTime() + _period;
			_alarms.add(this);
		}

		
		private void step(final Runnable stepper) {
			if (_isRunning) {
				my(Logger.class).log("Timer Alarm Skipped (was still running from previous time): ", stepper);
				return;
			}
			_isRunning = true;
			
			my(Threads.class).startDaemon("Timer for " + stepper, new Runnable() { @Override public void run() {
				stepper.run();
				_isRunning = false;
			}});
		}

		
		boolean isTimeToWakeUp() {
			return currentTime() >= _wakeUpTime;
		}
		
		@Override
		public int compareTo(Alarm alarm) {
			if (_wakeUpTime == alarm._wakeUpTime)
				return (int)(_sequence - alarm._sequence);
			return (int) (_wakeUpTime - alarm._wakeUpTime);
		}
		
		@Override
		public String toString() {
			return "Alarm: " + _wakeUpTime;
		}

		@Override
		public void dispose() {
			_isDisposed = true;
		}

	}

	
	private long currentTime() {
		return _clock.time().currentValue();
	}


}