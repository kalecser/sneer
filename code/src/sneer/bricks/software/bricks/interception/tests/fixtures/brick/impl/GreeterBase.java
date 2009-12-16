package sneer.bricks.software.bricks.interception.tests.fixtures.brick.impl;

import sneer.bricks.software.bricks.interception.tests.fixtures.brick.Greeter;

public class GreeterBase implements Greeter {

	@Override
	public String hello() {
		return "Hello!";
	}

}
