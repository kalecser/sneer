package sneer.bricks.hardware.cpu.threads.tests;

import static basis.environments.Environments.my;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import basis.brickness.Brickness;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.ClosureX;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;

public class CpuThrottleDemo {

	private static final Map<String, AtomicInteger> _countersByLabel = new HashMap<String, AtomicInteger>();


	public static void main(String[] ignored) {
		Environments.runWith(Brickness.newBrickContainer(), new Closure() { @Override public void run() {
			startChartingWithThrottle(5);
			startChartingWithThrottle(10);
			startChartingWithThrottle(20);
			
			my(Threads.class).sleepWithoutInterruptions(10000);
		}});
	}


	private static void startChartingWithThrottle(final int maxCpuUsage) {
		my(CpuThrottle.class).limitMaxCpuUsage(maxCpuUsage, new ClosureX<RuntimeException>(){ @Override public void run() throws RuntimeException {
			startCharting("" + maxCpuUsage + "%");
		}});
	}

	synchronized
	private static void startCharting(final String label) {
		_countersByLabel.put(label, new AtomicInteger());
		my(Threads.class).startStepping(new Closure(){ @Override public void run() {
			_countersByLabel.get(label).incrementAndGet();
			chart();
		}});
	}


	synchronized
	private static void chart() {
		System.out.println();
		for (Map.Entry<String, AtomicInteger> entry : _countersByLabel.entrySet())
			System.out.println("Stepper with CPU throttle at " + entry.getKey() + " ran " + entry.getValue() + " times.");
	}


}
