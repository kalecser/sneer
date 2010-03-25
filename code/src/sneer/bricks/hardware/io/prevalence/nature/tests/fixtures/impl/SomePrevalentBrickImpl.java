package sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.impl;

import sneer.bricks.hardware.io.prevalence.nature.tests.fixtures.SomePrevalentBrick;

class SomePrevalentBrickImpl implements SomePrevalentBrick {

	private String _string;

	@Override
	public String get() {
		return _string;
	}

	@Override
	public void set(String string) {
		_string = string;
	}
	
	
}