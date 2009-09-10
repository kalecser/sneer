package sneer.bricks.pulp.reactive.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Predicate;


public class SignalUtilsTest extends BrickTest {
	
	@Test (timeout = 2000)
	public void waitForExistingElementWithPredicate() {
		SetRegister<String> setRegister = my(CollectionSignals.class).newSetRegister();
		setRegister.add("foo");
		my(SignalUtils.class).waitForElement(setRegister.output(), new Predicate<String>() { @Override public boolean evaluate(String value) {
			return value.equals("foo");
		}});
		
	}

	@Test (timeout = 2000)
	public void waitForNewElementWithPredicate() {
		final SetRegister<String> setRegister = my(CollectionSignals.class).newSetRegister();
		
		my(Threads.class).startDaemon("SignalUtils Test", new Runnable() { @Override public void run() {
			my(Threads.class).sleepWithoutInterruptions(200);
			setRegister.add("foo");
		}});
		
		my(SignalUtils.class).waitForElement(setRegister.output(), new Predicate<String>() { @Override public boolean evaluate(String value) {
			return value.equals("foo");
		}});
		
	}

}
