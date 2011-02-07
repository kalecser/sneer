package sneer.bricks.software.bricks.interception.fixtures.combinedmethods.impl;


class Greeter2 extends GreeterBase {

	@Override
	public String hello() {
		return super.hello() + "!!";
	}

}
