package sneer.bricks.software.bricks.interception.tests.fixtures.refmethods.noargs.impl;

import sneer.bricks.software.bricks.interception.tests.fixtures.refmethods.noargs.RefMethodsNoArgs;

public class RefMethodsNoArgsImpl implements RefMethodsNoArgs {

	@Override
	public String bar() {
		return "42";
	}

}
