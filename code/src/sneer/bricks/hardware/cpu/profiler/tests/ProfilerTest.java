package sneer.bricks.hardware.cpu.profiler.tests;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

@Ignore
public class ProfilerTest extends BrickTestBase {

	//private final Profiler _subject = my(Profiler.class);

	@Test //(timeout = 2000)
	public void testProfiling() {
//		MapSignal<String, Float> profileResult = _subject.percentagesByMethod();
//		my(SignalUtils.class).waitForElement(profileResult.keys(), getClass().getName() + "testProfiling");
		sleepAWhile();
	}

	
	private void sleepAWhile() {
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}
	
}
