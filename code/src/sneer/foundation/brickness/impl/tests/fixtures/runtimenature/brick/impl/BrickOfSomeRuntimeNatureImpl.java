package sneer.foundation.brickness.impl.tests.fixtures.runtimenature.brick.impl;

import sneer.foundation.brickness.impl.tests.fixtures.runtimenature.brick.BrickOfSomeRuntimeNature;

public class BrickOfSomeRuntimeNatureImpl implements BrickOfSomeRuntimeNature {

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

}
