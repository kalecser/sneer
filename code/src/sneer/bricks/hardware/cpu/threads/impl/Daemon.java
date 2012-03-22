package sneer.bricks.hardware.cpu.threads.impl;

import static basis.environments.Environments.my;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.util.concurrent.Latch;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;


class Daemon extends Thread {

	private final Environment _environment = my(Environment.class);
	private int _maxCpuUsage = my(CpuThrottle.class).maxCpuUsage();
	private final String _context = my(StackTraceLogger.class).stackTrace();

	private final Closure _runnable;
	private final Latch _hasStarted;

	
	Daemon(String threadName, Closure runnable) {
		super(threadName);
		setDaemon(true);

		_runnable = runnable;
		
		_hasStarted = new Latch();
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
