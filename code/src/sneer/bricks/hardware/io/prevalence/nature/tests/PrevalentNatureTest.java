package sneer.bricks.hardware.io.prevalence.nature.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.SomePrevalentBrick;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class PrevalentNatureTest extends BrickTest {

	@Test (timeout = 2000)
	public void stateIsPreserved() {
		runInNewEnvironment(new Closure() { @Override public void run() {
			my(SomePrevalentBrick.class).set("foo");
		}});

		runInNewEnvironment(new Closure() { @Override public void run() {
			assertEquals("foo", my(SomePrevalentBrick.class).get());
		}});
	}

	private void runInNewEnvironment(Closure closure) {
		Environment newEnvironment = newTestEnvironment(my(FolderConfig.class));
		Environments.runWith(newEnvironment, closure);
		crash(newEnvironment);
	}

}
