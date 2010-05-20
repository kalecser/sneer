package sneer.bricks.software.bricks.interception.tests.fixtures.brick.impl;

import sneer.bricks.software.bricks.interception.tests.fixtures.brick.BrickOfSomeInterceptingNature;
import sneer.bricks.software.bricks.interception.tests.fixtures.brick.Greeter;

public class BrickOfSomeInterceptingNatureImpl implements BrickOfSomeInterceptingNature {

	@Override
	public int add(int i, int j) {
		return i + j;
	}

	@Override
	public String bar() {
		return "42";
	}
	
	@Override
	public int baz() {
		return 42;
	}

	@Override
	public Greeter newGreeter() {
		return new Greeter2();
	}
	
	@SuppressWarnings("unused")
	private void privateMethod() {
		
	}
	
	void packageMethod() {
		
	}
	
	protected void protectedMethod() {
		
	}

	@Override
	public void intraBrickMethod() {
		add(1, 1);
	}

}
