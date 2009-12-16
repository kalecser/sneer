package sneer.bricks.software.bricks.interception.tests.fixtures.brick.impl;


public class Greeter2 extends GreeterBase {

	@Override
	public String hello() {
		return super.hello() + "!!";
	}

}
