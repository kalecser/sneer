package sneer.foundation.brickness.impl.tests.fixtures.a;

import sneer.foundation.brickness.Brick;
import sneer.foundation.environments.Environment;

@Brick
public interface BrickA {

	Environment instantiationEnvironment();

	ClassLoader classLoader();
	ClassLoader libsClassLoader();
	
}
