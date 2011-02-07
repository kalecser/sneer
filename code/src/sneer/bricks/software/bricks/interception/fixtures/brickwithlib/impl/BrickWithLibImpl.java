package sneer.bricks.software.bricks.interception.fixtures.brickwithlib.impl;

import sneer.bricks.software.bricks.interception.fixtures.brickwithlib.BrickWithLib;

public class BrickWithLibImpl implements BrickWithLib {

	@Override
	public int fooBar() {
		return useLibType(new foo.Foo());
	}

	public int useLibType(foo.Foo foo) {
		return foo.bar();
	}
}
