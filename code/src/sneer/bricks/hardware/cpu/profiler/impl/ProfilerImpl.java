package sneer.bricks.hardware.cpu.profiler.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.pulp.blinkinglights.LightType.ERROR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.profiler.Profiler;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.software.folderconfig.FolderConfig;

class ProfilerImpl implements Profiler {

	private static final int TEN_SECONDS = 1000 * 10;
	private static final String[] STRING_ARRAY = new String[0];

	@SuppressWarnings("unused") private final WeakContract refToAvoidGc = my(Timer.class).wakeUpNowAndEvery(TEN_SECONDS, new Runnable() {  @Override public void run() {
		activateIfTriggerFileExists();
	}});
	
	
	private void activateIfTriggerFileExists() {
		if (!triggerFile().exists()) return;
		triggerFile().delete();
		
		File folder = triggerFile().getParentFile();
		File outputFile = new File(folder, "profiler" + System.currentTimeMillis() + ".txt");
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream(outputFile));
		} catch (FileNotFoundException e) {
			my(BlinkingLights.class).turnOn(ERROR, "Profiler Error", e.getMessage(), e);
			return;
		}

		Sampler sampler = new Sampler();
		my(Threads.class).sleepWithoutInterruptions(1000 * 60);
		sampler.stop();
		
		dumpTo(sampler, out);
		out.close();
	}
	

	private void dumpTo(final Sampler sampler, PrintStream out) {
		String[] methods = sampler.countByMethod.keySet().toArray(STRING_ARRAY);
		
		Arrays.sort(methods, new Comparator<String>() {  @Override public int compare(String m1, String m2) {
			AtomicInteger count1 = sampler.countByMethod.get(m1);
			AtomicInteger count2 = sampler.countByMethod.get(m2);
			int diff = count2.get() - count1.get();
			return diff != 0 ? diff : m1.compareTo(m2);
		}});
		
		for (String method : methods)
			methodLine(sampler, out, method);
		out.println("\n\n");
	}


	private void methodLine(final Sampler sampler, PrintStream out, String method) {
		AtomicInteger count = sampler.countByMethod.get(method);
		long loadPercentage = 100 * count.get() / sampler.sampleCount;
		out.println(count.get() + "\t" + loadPercentage + "%\t" + method);
	}

	
	private static File triggerFile() {
		File tmpFolder = my(FolderConfig.class).tmpFolder().get();
		return new File(tmpFolder, "profiler");
	}

}
