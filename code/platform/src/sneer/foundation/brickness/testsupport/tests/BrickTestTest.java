package sneer.foundation.brickness.testsupport.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.io.log.tests.BrickTestWithLogger;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.brickness.testsupport.tests.bar.BarBrick;
import sneer.foundation.brickness.testsupport.tests.foo.FooBrick;

public class BrickTestTest extends BrickTestWithLogger {
	
	@Bind final BarBrick _bar = new BarBrick() {};
	
	final FooBrick _foo = my(FooBrick.class);
	
	@Test
	public void test() {
		BarBrick other = _foo.bar();
		assertSame(_bar, other);
	}

}
