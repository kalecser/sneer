package sneer.bricks.hardware.cpu.profiler.impl;

import static basis.environments.Environments.my;
import static java.lang.Thread.State.RUNNABLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import basis.lang.CacheMap;
import basis.lang.Closure;
import basis.lang.Producer;

import sneer.bricks.hardware.cpu.threads.Threads;


class Sampler {

	private static final Producer<AtomicInteger> NEW_ATOMIC_INT = new Producer<AtomicInteger>(){  @Override public AtomicInteger produce() {
		return new AtomicInteger();
	}};

	
	final CacheMap<String, AtomicInteger> countByMethod = CacheMap.newInstance();
	int sampleCount = 0;
	private volatile boolean stopped = false;

	{
		my(Threads.class).startDaemon("Profiler", new Closure() { @Override public void run() {
			while (!stopped) step();
		}});
	}
	
	
	private void step() {
		my(Threads.class).sleepWithoutInterruptions(10);
		takeSample();
	}

	
	synchronized
	void stop() {
		stopped = true;
	}
	
	
	private void takeSample() {
		Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
		List<Thread> active = new ArrayList<Thread>(stacks.size());
		for (Thread thread : stacks.keySet())
			if (thread.getState() == RUNNABLE)
				active.add(thread);
		for (Thread thread : active)
			countUniqueMethods(stacks.get(thread));
		sampleCount++;
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
		String previousMethod = null;
		for (int i = 0; i < methodCount; i++) {
			String method = sortedMethods[i];
			if (method.equals(previousMethod)) continue;
			previousMethod = method;
			countByMethod.get(method, NEW_ATOMIC_INT).incrementAndGet();
		}
	}
}
