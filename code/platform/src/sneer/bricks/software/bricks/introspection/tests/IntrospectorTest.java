package sneer.bricks.software.bricks.introspection.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.software.bricks.introspection.Introspector;

public class IntrospectorTest extends TestThatUsesLogger {
	
	@Test
	public void brickInterfaceFor() {
		final Introspector introspector = my(Introspector.class);
		assertSame(
				Introspector.class,
				introspector.brickInterfaceFor(introspector));
	}

}
