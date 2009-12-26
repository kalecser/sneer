package sneer.bricks.hardware.cpu.threads.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ProducerWithThrowable;

public class CpuThrottleDemo {

	private static final Map<String, AtomicInteger> _countersByLabel = new HashMap<String, AtomicInteger>();


	public static void main(String[] ignored) {
		Environments.runWith(Brickness.newBrickContainer(), new Runnable() { @Override public void run() {
			startChartingWithThrottle(5);
			startChartingWithThrottle(10);
			startChartingWithThrottle(20);
			
			my(Threads.class).sleepWithoutInterruptions(10000);
		}});
	}


	private static void startChartingWithThrottle(final int maxCpuUsage) {
		my(CpuThrottle.class).limitMaxCpuUsage(maxCpuUsage, new ProducerWithThrowable<Object, RuntimeException>(){ @Override public Object produce() throws RuntimeException {
			startCharting("" + maxCpuUsage + "%");
			return null;
		}});
	}


	private static void startCharting(final String label) {
		_countersByLabel.put(label, new AtomicInteger());
		my(Threads.class).startStepping(new Runnable(){ @Override public void run() {
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
