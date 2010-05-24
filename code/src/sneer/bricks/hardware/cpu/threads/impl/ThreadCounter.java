package sneer.bricks.hardware.cpu.threads.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;


class ThreadCounter {

	private static final CacheMap<Class<? extends Runnable>, Integer> _countByRunnableClass = CacheMap.newInstance();

	@SuppressWarnings("unused") private static WeakContract _refToAvoidGc;

	
	static {
		_refToAvoidGc = my(Timer.class).wakeUpEvery(5000, new Runnable() { @Override public void run() {
			log();
		}});
	}

	
	private static void log() {
		String list = "";
		for (Class<?> clazz : _countByRunnableClass.keySet().toArray(new Class<?>[0]))
			list += "\n\t" + clazz + ": " + _countByRunnableClass.get(clazz);
			
		my(Logger.class).log("Thread count by Runnable class:", list);
	}

	synchronized
	static void increment(Class<? extends Runnable> clazz) {
		Integer count = _countByRunnableClass.get(clazz, new Producer<Integer>() { @Override public Integer produce() {
			return 0;
		}});
		_countByRunnableClass.put(clazz, count + 1);
	}

	
	synchronized
	static void decrement(Class<? extends Runnable> clazz) {
		int count = _countByRunnableClass.get(clazz);
		if (count == 1)
			_countByRunnableClass.remove(clazz);
		else
			_countByRunnableClass.put(clazz, count - 1);
	}

}
