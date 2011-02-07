package sneer.bricks.software.bricks.interception.fixtures.combinedmethods.impl;

import sneer.bricks.software.bricks.interception.fixtures.combinedmethods.Greeter;

class GreeterBase implements Greeter {

	@Override
	public String hello() {
		return "Hello!";
	}

}
