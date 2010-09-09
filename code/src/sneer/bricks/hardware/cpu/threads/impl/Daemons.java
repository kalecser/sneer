package sneer.bricks.hardware.cpu.threads.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Daemons {

	synchronized static void killAllInstances() {
		List<Daemon> allDaemons = new ArrayList<Daemon>();
		
		for (Thread cadidate : allThreads())
			if (cadidate instanceof Daemon)
				allDaemons.add((Daemon)cadidate);

		for (Daemon victim : allDaemons)
			killQuietly(victim);

		for (Daemon victim : allDaemons)
			try {
				victim.join(2000); //Gives them a little time to die but does not wait if they are already dead.
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
	}

	
	private static Set<Thread> allThreads() {
		return Thread.getAllStackTraces().keySet();
	}

	
	@SuppressWarnings("deprecation")
	static void killQuietly(Thread thread) {
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() { @Override public void uncaughtException(Thread t, Throwable ignored) {
			//Shhhh.
		}});
		
		thread.stop();
	}

	
}
