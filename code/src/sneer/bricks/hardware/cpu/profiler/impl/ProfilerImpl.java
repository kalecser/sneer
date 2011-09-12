package sneer.bricks.hardware.cpu.profiler.impl;

import static java.lang.Thread.State.RUNNABLE;
import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.profiler.Profiler;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.MapRegister;
import sneer.bricks.pulp.reactive.collections.MapSignal;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class ProfilerImpl implements Profiler {

	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	private static final String[] STRING_ARRAY = new String[0];

	private MapRegister<String, Float> _percentagesByMethod = my(CollectionSignals.class).newMapRegister();
	private CacheMap<String, AtomicInteger> countByMethod = CacheMap.newInstance();

	private static final Producer<AtomicInteger> NEW_ATOMIC_INT = new Producer<AtomicInteger>(){  @Override public AtomicInteger produce() {
		return new AtomicInteger();
	}};
	@SuppressWarnings("unused") private final WeakContract refToAvoidGc = my(Timer.class).wakeUpEvery(FIVE_MINUTES, new Runnable() {  @Override public void run() {
		printSample();
	}});
	
	@Override
	public MapSignal<String, Float> percentagesByMethod() {
		return _percentagesByMethod.output();
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

	synchronized
	private void printSample() {
		PrintStream defaultPrinter = getDefaultPrinter(); 
		if (defaultPrinter != null) {
			takeSample();
			dumpTo(defaultPrinter);
			defaultPrinter.close();
		}	
	}

	private PrintStream getDefaultPrinter() {
		File defaultFile = getDefaultFile();

		if (defaultFile != null) {
			try {
				return new PrintStream(defaultFile);
			} catch (Exception e) {}
		}
		return null;		
	}
	
	private File getDefaultFile() {
		File tmpFolder = my(FolderConfig.class).tmpFolder().get();
		
		for(File file : tmpFolder.listFiles()) {
			if ("profiler.txt".equalsIgnoreCase(file.getName()))
				return file;
		}
		return null;
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
}
