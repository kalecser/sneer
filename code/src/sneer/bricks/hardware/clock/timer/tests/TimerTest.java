package sneer.bricks.hardware.clock.timer.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;

public class TimerTest extends BrickTestBase {

	private final Clock _clock = my(Clock.class);
	private final Timer _subject = my(Timer.class);
	
	@SuppressWarnings("unused")	private WeakContract _c1;
	@SuppressWarnings("unused")	private WeakContract _c2;
	@SuppressWarnings("unused")	private WeakContract _c3;
	@SuppressWarnings("unused")	private WeakContract _c4;
	@SuppressWarnings("unused")	private WeakContract _c5;
	
	@Test (timeout = 2000)
	public void testAlarms() throws Exception {
		final List<Integer> _order = new ArrayList<Integer>();
		
		CountDownLatch latch = new CountDownLatch(5);
		_c1 = _subject.wakeUpInAtLeast(50, new Worker(50, _order, latch));
		_c2 = _subject.wakeUpEvery(20, new Worker(20, _order, latch));
		_c3 = _subject.wakeUpInAtLeast(10, new Worker(10, _order, latch));
		_c4 = _subject.wakeUpEvery(35, new Worker(35, _order, latch));
		_c5 = _subject.wakeUpInAtLeast(30, new Worker(30,_order, latch));
		
		_clock.advanceTime(81);
		
		latch.await();
	}

	@Test
	public void testSimultaneousAlarms() throws Exception {
		final List<Integer> _order = new ArrayList<Integer>();
		
		CountDownLatch latch = new CountDownLatch(2);
		_c1 = _subject.wakeUpInAtLeast(10, new Worker(10, _order, latch));
		_c2 = _subject.wakeUpInAtLeast(10, new Worker(10, _order, latch));
		
		_clock.advanceTime(10);
		
		latch.await();
	}

	@Test (timeout = 6000)
	public void testContractWeakness() throws Exception {
		final ByRef<Boolean> finalized = ByRef.newInstance(false);
		
		_subject.wakeUpEvery(42, new Closure() {
			@Override
			public void run() {
				return;
			}

			@Override
			protected void finalize() throws Throwable {
				finalized.value = true; 
			}
		});

		while (!finalized.value) {
			System.gc();
			Thread.sleep(20);
			_clock.advanceTime(42);
		}
	}

	
	private class Worker implements Runnable {

		private final int _timeout;
		private final List<Integer> _order;
		private int _count = 0;
		private final CountDownLatch _latch;

		public Worker(int timeout, List<Integer> order, CountDownLatch latch) {
			_timeout = timeout;
			_order = order;
			_latch = latch;
		}

		@Override
		public void run() {
			_count++;
			_order.add(_timeout * _count);
			_latch.countDown();
		}
	}
	
	@Test
	public void testAlarmThatAddsAlarm() throws Exception {
		final Latch latch1 = my(Latches.class).produce();
		final Latch latch2 = my(Latches.class).produce();
		
		_c1 = _subject.wakeUpInAtLeast(1, new Closure() { @Override public void run() {
			_c2 = _subject.wakeUpInAtLeast(1, new Closure() { @Override public void run() {
				latch2.open();
			}});
			latch1.open();
		}});
		
		_clock.advanceTime(2);
		latch1.waitTillOpen();

		_clock.advanceTime(1);
		latch2.waitTillOpen();
	}

}
