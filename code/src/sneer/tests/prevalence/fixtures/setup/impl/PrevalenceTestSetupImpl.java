package sneer.tests.prevalence.fixtures.setup.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.SomePrevalentBrick;
import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.brick2.PrevalentBrick2;
import sneer.tests.prevalence.fixtures.setup.PrevalenceTestSetup;

public class PrevalenceTestSetupImpl implements PrevalenceTestSetup {{

	my(SomePrevalentBrick.class).addItem("Foo");
	my(PrevalentBrick2.class).rememberItemCount();
	
}}
