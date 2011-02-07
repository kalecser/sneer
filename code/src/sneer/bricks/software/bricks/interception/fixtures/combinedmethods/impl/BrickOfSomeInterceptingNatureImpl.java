package sneer.bricks.software.bricks.interception.fixtures.combinedmethods.impl;

import sneer.bricks.software.bricks.interception.fixtures.combinedmethods.BrickOfSomeInterceptingNature;
import sneer.bricks.software.bricks.interception.fixtures.combinedmethods.Greeter;

class BrickOfSomeInterceptingNatureImpl implements BrickOfSomeInterceptingNature {

	@Override
	public int add(int i, int j) {
		return i + j;
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
