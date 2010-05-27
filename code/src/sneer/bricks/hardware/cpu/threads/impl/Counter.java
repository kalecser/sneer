package sneer.bricks.hardware.cpu.threads.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;


class Counter {

	private static final CacheMap<String, Integer> _countByKey = CacheMap.newInstance();

	@SuppressWarnings("unused") private static WeakContract _refToAvoidGc;

	
	static {
		_refToAvoidGc = my(Timer.class).wakeUpEvery(5000, new Runnable() { @Override public void run() {
			log();
		}});
	}

	
	private static void log() {
		String list = "";
		for (String key : _countByKey.keySet().toArray(new String[0]))
			list += "\n\t" + key + ": " + _countByKey.get(key);
			
		my(Logger.class).log("Thread count by purpose:", list);
	}

	synchronized
	static void increment(String key) {
		Integer count = _countByKey.get(key, new Producer<Integer>() { @Override public Integer produce() {
			return 0;
		}});
		_countByKey.put(key, count + 1);
	}

	
	synchronized
	static void decrement(String key) {
		int count = _countByKey.get(key);
		if (count == 1)
			_countByKey.remove(key);
		else
			_countByKey.put(key, count - 1);
	}

}
