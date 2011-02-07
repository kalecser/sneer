package sneer.bricks.software.bricks.interception.fixtures.staticinitializer.impl;

import sneer.bricks.software.bricks.interception.fixtures.staticinitializer.StaticInitializer;

class StaticInitializerImpl implements StaticInitializer {
	
	static { nop(); }
	
	static void nop() {
		System.out.print("");
	}
	
	@Override
	public void foo() {}

}
