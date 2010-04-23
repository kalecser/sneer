package sneer.bricks.hardware.io.prevalence.nature.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.SomePrevalentBrick;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class NullParameterTest extends BrickTest {

	@Test (timeout = 2000)
	public void nullParameter() {
		my(SomePrevalentBrick.class).set("foo");
		my(SomePrevalentBrick.class).set(null);

		assertNull(my(SomePrevalentBrick.class).get());
	}

}
