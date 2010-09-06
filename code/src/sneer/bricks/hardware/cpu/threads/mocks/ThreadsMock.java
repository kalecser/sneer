package sneer.bricks.hardware.cpu.threads.mocks;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.pulsers.PulseSource;
import sneer.foundation.brickness.impl.BricknessImpl;

public class ThreadsMock implements Threads {

	private final Threads _delegate = my(BricknessImpl.class).provide(Threads.class);
	
	List<Runnable> _steppers = new ArrayList<Runnable>();
	
	private final String _daemonNameFragmentToHold;
	private Map<Runnable, String> _daemonNamesByRunnable = new HashMap<Runnable, String>();

	private final EventNotifier<Object> _crashingPulser = my(EventNotifiers.class).newInstance();


	public ThreadsMock(String daemonNameFragmentToHold) {
		_daemonNameFragmentToHold = daemonNameFragmentToHold;
	}
	

	@Override
	public synchronized Contract startStepping(Runnable stepper) {
		_steppers.add(stepper);
		return null;
	}

	
	@Override
	public synchronized Contract startStepping(String threadNameIgnored, Runnable stepper) {
		return startStepping(stepper);
	}

	public synchronized Runnable getStepper(int i) {
		return _steppers.get(i);
	}

	public synchronized void runDaemonWithNameContaining(String partOfName) {
		Collection<Runnable> daemonsCopy = new ArrayList<Runnable>(_daemonNamesByRunnable.keySet());

		boolean wasRun = false;
		
		for (Runnable daemon : daemonsCopy) {
			String daemonName = _daemonNamesByRunnable.get(daemon);
			if (daemonName.indexOf(partOfName) == -1) continue;
			
			_daemonNamesByRunnable.remove(daemon);
			daemon.run();
			if (wasRun) throw new IllegalStateException("Found more than one daemon named: " + partOfName);
			wasRun = true;
		}
		
		if (!wasRun) throw new IllegalStateException("Daemon not found: " + partOfName);
	}

	@Override
	public void joinWithoutInterruptions(Thread thread) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}


	@Override
	public void sleepWithoutInterruptions(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void startDaemon(String threadName, Runnable runnable) {
		if (threadName.indexOf(_daemonNameFragmentToHold) == -1)
			_delegate.startDaemon(threadName, runnable);
		else
			_daemonNamesByRunnable.put(runnable, threadName);
	}

	
	@Override
	public void waitWithoutInterruptions(Object object) {
		try {
			object.wait();

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void waitUntilCrash() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public void crashAllThreads() {
		_crashingPulser.notifyReceivers(null);
	}

	@Override
	public PulseSource crashed() {
		return _crashingPulser.output();
	}

}