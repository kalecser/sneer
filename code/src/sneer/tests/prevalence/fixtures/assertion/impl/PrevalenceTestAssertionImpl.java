package sneer.tests.prevalence.fixtures.assertion.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.brick2.PrevalentBrick2;
import sneer.tests.prevalence.fixtures.assertion.PrevalenceTestAssertion;

public class PrevalenceTestAssertionImpl implements PrevalenceTestAssertion {{

	if (my(PrevalentBrick2.class).recallItemCount() != 1)
		throw new IllegalStateException();
	
}}
