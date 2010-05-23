package sneer.bricks.software.bricks.interception.tests.fixtures.staticinitializer.impl;

import sneer.bricks.software.bricks.interception.tests.fixtures.staticinitializer.StaticInitializer;

class StaticInitializerImpl implements StaticInitializer {
	
	{ nop(); }
	
	static void nop() {}
	
	@Override
	public void foo() {}

}
