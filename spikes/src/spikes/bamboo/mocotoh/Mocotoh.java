package spikes.bamboo.mocotoh;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.TestMethod;

import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;

public class Mocotoh extends JUnit4ClassRunner {
	
	public Mocotoh(Class<?> testClass) throws InitializationError {
		super(testClass);
	}
	
	@Override
	protected TestMethod wrapMethod(Method method) {
		return new TestMethod(method, this.getTestClass()) {
			@Override
			public void invoke(final Object test) {
				try {
					Environments.runWith(Brickness.newBrickContainer(), new ClosureX<Exception>() { @Override public void run() throws Exception {
						superInvoke(test);
					}});
				} catch (Exception e) {
					throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
				}
			}
			
			private void superInvoke(Object test) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
				super.invoke(test);
			}
		};
	}
}
