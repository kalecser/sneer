package sneer.bricks.hardware.io.prevalence.nature.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.SomePrevalentBrick;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class PrevalentNatureTest extends BrickTest {
	
	Environment subject = Brickness.newBrickContainer();
	
	@Test (timeout = 2000)
	public void stateIsPreserved() {
		
		Environments.runWith(newTestEnvironment(my(FolderConfig.class)), new Closure() { @Override public void run() {
			my(SomePrevalentBrick.class).set("foo");
		}});

		Environments.runWith(newTestEnvironment(my(FolderConfig.class)), new Closure() { @Override public void run() {
			assertEquals("foo", my(SomePrevalentBrick.class).get());
		}});
	}

}
