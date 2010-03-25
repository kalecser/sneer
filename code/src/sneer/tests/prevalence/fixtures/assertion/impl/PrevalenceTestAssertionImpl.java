package sneer.tests.prevalence.fixtures.assertion.impl;

import static sneer.foundation.environments.Environments.my;

import org.junit.Assert;

import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.brick2.PrevalentBrick2;
import sneer.tests.prevalence.fixtures.assertion.PrevalenceTestAssertion;

public class PrevalenceTestAssertionImpl implements PrevalenceTestAssertion {{

	Assert.assertEquals(1, my(PrevalentBrick2.class).recallItemCount());
	
}}
