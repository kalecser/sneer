package sneer.foundation.brickness.impl.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Assert;
import org.junit.Test;

import sneer.foundation.brickness.Brickness;
import sneer.foundation.brickness.impl.tests.fixtures.a.BrickA;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class BricknessEnvironmentTest extends Assert {
	
	Environment _subject = Brickness.newBrickContainer();

	
	@Test
	public void brickInstantiationPreservesEnvironment() throws Exception {
		Environments.runWith(_subject, new Closure() { @Override public void run() {
			assertSame(_subject, my(BrickA.class).instantiationEnvironment());
		}});
	}

}
