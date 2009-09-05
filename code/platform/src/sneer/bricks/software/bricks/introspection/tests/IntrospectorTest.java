package sneer.bricks.software.bricks.introspection.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.io.log.tests.BrickTestWithLogger;
import sneer.bricks.software.bricks.introspection.Introspector;

public class IntrospectorTest extends BrickTestWithLogger {
	
	@Test
	public void brickInterfaceFor() {
		final Introspector introspector = my(Introspector.class);
		assertSame(
				Introspector.class,
				introspector.brickInterfaceFor(introspector));
	}

}
