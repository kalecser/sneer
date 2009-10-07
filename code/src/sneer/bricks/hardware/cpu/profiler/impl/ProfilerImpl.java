package sneer.bricks.hardware.cpu.profiler.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.profiler.Profiler;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.MapRegister;
import sneer.bricks.pulp.reactive.collections.MapSignal;
import sneer.foundation.lang.exceptions.NotImplementedYet;

class ProfilerImpl implements Profiler {

	private MapRegister<String, Float> _percentagesByMethod = my(CollectionSignals.class).newMapRegister();
	private Thread[] _allThreads = new Thread[1];

	{
		my(Threads.class).startDaemon("Profiler", new Runnable() { @Override public void run() {
			generateProfile();
		}});
	}
	
	@Override
	public MapSignal<String, Float> percentagesByMethod() {
		return _percentagesByMethod.output();
	}

	private void generateProfile() {
		int count;
		while (true) {
			count = Thread.enumerate(_allThreads);
			if (count < _allThreads.length) break;
			_allThreads = new Thread[_allThreads.length * 2];
		}
		throw new NotImplementedYet();
	}

}
