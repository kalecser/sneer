package sneer.bricks.pulp.bandwidth.impl;

import static basis.environments.Environments.my;
import basis.lang.Closure;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.bandwidth.BandwidthCounter;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class BandwidthCounterImpl implements BandwidthCounter {

	static private final int CONSOLIDATION_TIME = 1000;
	
	static private final Clock Clock = my(Clock.class);
	private long _lastConsolidationTime = Clock.time().currentValue();
	
	private int _upCounter = 0;
	private int _dnCounter = 0;
	
	private final Register<Integer> _upSpeed = my(Signals.class).newRegister(0); 
	private final Register<Integer> _dnSpeed = my(Signals.class).newRegister(0); 

	@SuppressWarnings("unused") private final WeakContract _alarmContract = my(Timer.class).wakeUpEvery(CONSOLIDATION_TIME, new Closure(){ @Override public void run() {
		consolidate();
	}});
	
	
	@Override public Signal<Integer> uploadSpeedInKBperSecond()   { return _upSpeed.output(); }
	@Override public Signal<Integer> downloadSpeedInKBperSecond() { return _dnSpeed.output(); }
	
	@Override synchronized public void sent    (int byteCount) { _upCounter += byteCount; }
	@Override synchronized public void received(int byteCount) { _dnCounter += byteCount; }
	
	
	synchronized
	private final void consolidate() {
		long currentTime = Clock.time().currentValue();
		long deltaMillis = currentTime - _lastConsolidationTime;
		if (deltaMillis == 0) return; //This might happen when the clock is mocked.
		_lastConsolidationTime = currentTime;
		
		setKBytesPerSecond(_upSpeed, _upCounter, deltaMillis);
		setKBytesPerSecond(_dnSpeed, _dnCounter, deltaMillis);
		
		_upCounter = 0;
		_dnCounter = 0;
	}

	
	private void setKBytesPerSecond(Register<Integer> speed, int byteCount, long deltaMillis) {
		int value = (int)(byteCount * 1000 / deltaMillis / 1024);
		speed.setter().consume(value);
	}

}