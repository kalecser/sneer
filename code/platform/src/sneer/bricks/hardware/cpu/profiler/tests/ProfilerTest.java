package sneer.bricks.hardware.cpu.profiler.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.cpu.profiler.Profiler;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.collections.MapSignal;
import sneer.foundation.brickness.testsupport.BrickTest;

public class ProfilerTest extends BrickTest {

	private final Profiler _subject = my(Profiler.class);

	@Ignore
	@Test (timeout = 2000)
	public void testProfiling() {
		MapSignal<String, Float> profileResult = _subject.percentagesByMethod();
		my(SignalUtils.class).waitForElement(profileResult.keys(), getClass().getName() + "testProfiling");
	}
	
}
