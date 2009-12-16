package sneer.bricks.software.bricks.interception.tests.fixtures.brick.impl;

import sneer.bricks.software.bricks.interception.tests.fixtures.brick.BrickOfSomeInterceptingNature;
import sneer.bricks.software.bricks.interception.tests.fixtures.brick.Greeter;

public class BrickOfSomeInterceptingNatureImpl implements BrickOfSomeInterceptingNature {

	@Override
	public void foo() {
		throw new IllegalStateException();
	}
	
	@Override
	public void foo(String arg) {
		throw new IllegalStateException();
	}
	
	@Override
	public int add(int i, int j) {
		return i + j;
	}

	@Override
	public String bar() {
		throw new IllegalStateException();
	}
	
	@Override
	public int baz() {
		throw new IllegalStateException();
	}

	@Override
	public Greeter newGreeter() {
		return new Greeter2();
	}

}
