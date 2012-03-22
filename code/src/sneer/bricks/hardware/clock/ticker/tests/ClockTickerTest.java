package sneer.bricks.hardware.clock.ticker.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import basis.lang.Consumer;
import basis.util.concurrent.Latch;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;


public class ClockTickerTest extends BrickTestBase {

	private final Clock _clock = my(Clock.class);

	{
		my(ClockTicker.class);
	}

	@Test (timeout = 3000)
	public void testTicking() {
		waitForATick();
		waitForATick();
	}

	private void waitForATick() {
		final Latch latch = new Latch();
		@SuppressWarnings("unused")
		WeakContract contract = _clock.time().addReceiver(new Consumer<Long>() { @Override public void consume(Long value) {
			latch.open();
		}});
		latch.waitTillOpen();
	}
	
}
