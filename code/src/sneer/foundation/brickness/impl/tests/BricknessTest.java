package sneer.foundation.brickness.impl.tests;

import org.junit.Assert;
import org.junit.Test;

import sneer.foundation.brickness.BrickLoadingException;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.brickness.impl.BricknessImpl;
import sneer.foundation.brickness.impl.tests.fixtures.InterfaceWithoutBrickAnnotation;
import sneer.foundation.brickness.impl.tests.fixtures.InterfaceWithoutImplementation;
import sneer.foundation.brickness.impl.tests.fixtures.a.BrickA;
import sneer.foundation.brickness.impl.tests.fixtures.b.BrickB;
import sneer.foundation.environments.Environment;


public class BricknessTest extends Assert {
	
	Environment _subject = Brickness.newBrickContainer();

	
	@Test
	public void environmentProvision() {
		assertSame(_subject, provide(BricknessImpl.class));
		assertSame(_subject, provide(Environment.class));
	}


	@Test
	public void brickProvision() throws Exception {
		assertNotNull(provide(BrickA.class));
	}
	

	@Test(expected = BrickLoadingException.class)
	public void noBrickInterfaceFound() throws Exception {
		provide(InterfaceWithoutBrickAnnotation.class);
	}

	
	@Test(expected = BrickLoadingException.class)
	public void interfaceWithoutImpl() {
		provide(InterfaceWithoutImplementation.class);
	}

	
	@Test
	public void bricksHaveSeparateClassloaders() throws Exception {
		BrickA a = provide(BrickA.class);
		BrickB b = provide(BrickB.class);

		assertNotSame(a.classLoader(), b.classLoader());
	}
	
	
	@Test
	public void brickLibsHaveSeparateClassloaders() throws Exception {
		BrickA a = provide(BrickA.class);
		BrickB b = provide(BrickB.class);

		assertNotSame(a.libsClassLoader(), b.libsClassLoader());
		assertNotSame(a.libsClassLoader(), a.classLoader());
	}

	
	private <T> T provide(final Class<T> brick) {
		return _subject.provide(brick);
	}

}
