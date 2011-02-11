package sneer.bricks.hardware.cpu.threads.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;


class Counter {

	private static final CacheMap<String, Integer> _countByKey = CacheMap.newInstance();
	private static int _count = 0;
	private static int _countPeak;

	
	private static void log() {
		String list = "";
		for (Entry<String, Integer> entry : entriesSortedByCountThenKey())
			list += "\n  " + entry.getValue() + ": " + entry.getKey();
			
		my(Logger.class).log("Thread count by purpose:", list);
	}

	
	private static Map.Entry<String, Integer>[] entriesSortedByCountThenKey() {
		Entry<String, Integer>[] result = _countByKey.entrySet().toArray(new Map.Entry[0]);
		Arrays.sort(result, new Comparator<Entry<String, Integer>>() { @Override public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
			int countDif = entry2.getValue().compareTo(entry1.getValue()); //Sort by count, decreasing
			return countDif == 0 ? entry1.getKey().compareTo(entry2.getKey()): countDif; //Then, sort by key, increasing.
		}});
		return result;
	}

	
	synchronized
	static void increment(String key) {
		incrementByKey(key);
		_count++;
		checkPeakReached();
	}

	
	private static void checkPeakReached() {
		if (_count <= _countPeak) return;
		_countPeak = (int)(_count * 1.3);
		log();
	}

	
	private static void incrementByKey(String key) {
		Integer count = _countByKey.get(key, new Producer<Integer>() { @Override public Integer produce() {
			return 0;
		}});
		_countByKey.put(key, count + 1);
	}

	
	synchronized
	static void decrement(String key) {
		_count--;
		decrementByKey(key);
	}

	
	private static void decrementByKey(String key) {
		int count = _countByKey.get(key);
		if (count == 1)
			_countByKey.remove(key);
		else
			_countByKey.put(key, count - 1);
	}

}
