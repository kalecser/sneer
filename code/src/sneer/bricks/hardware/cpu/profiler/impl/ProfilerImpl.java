package sneer.bricks.hardware.cpu.profiler.impl;

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.State.RUNNABLE;
import static sneer.foundation.environments.Environments.my;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import sneer.bricks.hardware.cpu.profiler.Profiler;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.MapRegister;
import sneer.bricks.pulp.reactive.collections.MapSignal;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

class ProfilerImpl implements Profiler {

	private static final String[] STRING_ARRAY = new String[0];
	private static final Producer<AtomicInteger> NEW_ATOMIC_INT = new Producer<AtomicInteger>(){  @Override public AtomicInteger produce() {
		return new AtomicInteger();
	}};
	private MapRegister<String, Float> _percentagesByMethod = my(CollectionSignals.class).newMapRegister();
	private CacheMap<String, AtomicInteger> countByMethod = CacheMap.newInstance();

	{
		my(Threads.class).startDaemon("Profiler", new Closure() { @Override public void run() {
			Thread.currentThread().setPriority(MAX_PRIORITY);
			while (true) {
				takeSample();
				sleepABit();
			}
		}});
	}
	
	@Override
	public MapSignal<String, Float> percentagesByMethod() {
		return _percentagesByMethod.output();
	}


	synchronized
	private void takeSample() {
		Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
		List<Thread> active = new ArrayList<Thread>(stacks.size());
		for (Thread thread : stacks.keySet())
			if (thread.getState() == RUNNABLE)
				active.add(thread);
		for (Thread thread : active)
			countUniqueMethods(stacks.get(thread));
	}

	
	private void countUniqueMethods(StackTraceElement[] stack) {
		int methodCount = 0;
		String[] methods = new String[stack.length];
		for (int i = 0; i < stack.length; i++) {
			StackTraceElement element = stack[i];
			String className = element.getClassName();
			if (!className.startsWith("sneer")) continue;
			String method = className + "#" + element.getMethodName();
			methods[methodCount++] = method;
		}
		Arrays.sort(methods, 0, methodCount);
		countUniqueMethods(methods, methodCount);
	}
	

	private void countUniqueMethods(String[] sortedMethods, int methodCount) {
		if (methodCount == 0) return;
		String previousMethod = null;
		for (int i = 0; i < methodCount; i++) {
			String method = sortedMethods[i];
			if (method.equals(previousMethod)) continue;
			previousMethod = method;
			countByMethod.get(method, NEW_ATOMIC_INT).incrementAndGet();
		}
	}

	private void sleepABit() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	synchronized
	public void dumpTo(PrintStream out) {
		String[] methods = countByMethod.keySet().toArray(STRING_ARRAY);
		
		Arrays.sort(methods, new Comparator<String>() {  @Override public int compare(String m1, String m2) {
			AtomicInteger count1 = countByMethod.get(m1);
			AtomicInteger count2 = countByMethod.get(m2);
			return count1.get() - count2.get();
		}});
		
		for (String method : methods) {
			AtomicInteger count = countByMethod.get(method);
			out.println(count + "\t" + method);
		}
		out.println("\n\n");
	}

}
