package sneer.bricks.software.bricks.interception.fixtures.primitivemethods.noargs.impl;

import sneer.bricks.software.bricks.interception.fixtures.primitivemethods.noargs.PrimitiveMethodNoArgs;

public class PrimitiveMethodNoArgsImpl implements PrimitiveMethodNoArgs {

	@Override
	public int baz() {
		return 42;
	}

}
