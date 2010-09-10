package sneer.bricks.hardware.cpu.threads.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;


class Daemon extends Thread {

	private static final Latches Latches = my(Latches.class);
	
	
	private final Environment _environment = my(Environment.class);
	private int _maxCpuUsage = my(CpuThrottle.class).maxCpuUsage();
	private final String _context = my(StackTraceLogger.class).stackTrace();

	private final Closure _runnable;
	private final Latch _hasStarted;

	
	Daemon(String threadName, Closure runnable) {
		super(threadName);
		setDaemon(true);

		_runnable = runnable;
		
		_hasStarted = Latches.produce();
		start();
		_hasStarted.waitTillOpen();
	}


	@Override
	public void run() {
		_hasStarted.open();
		
		Environments.runWith(_environment, new Closure() { @Override public void run() {
			Counter.increment(getName());
			shield();
			Counter.decrement(getName());
		}});
	}


	private void shield() {
		try {
			my(CpuThrottle.class).limitMaxCpuUsage(_maxCpuUsage, _runnable);
		} catch (ThreadDeath death) {
			throw death;
		} catch (Throwable t) {
			my(ExceptionLogger.class).log(t, "Exception in thread " + getName() + " started by:\n" + _context);
		}
	}
	
}
