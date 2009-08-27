package sneer.foundation.testsupport;

import java.lang.reflect.Method;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class CleanTestRunner extends JUnit4ClassRunner {

	private Object _currentTest;

	public CleanTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}
	
	@Override
	protected Object createTest() throws Exception {
		_currentTest = super.createTest();
		return _currentTest;
	}

	@Override
	protected void invokeTestMethod(Method method, RunNotifier notifier) {
		RunListener listener = new RunListener() { @Override public void testFailure(Failure failure) throws Exception {
			notifyFailure();
		}};
			
		notifier.addListener(listener);
		super.invokeTestMethod(method, notifier);
		notifier.removeListener(listener);
			
		_currentTest = null;
	}

	private void notifyFailure() {
		if (_currentTest instanceof CleanTest)
			((CleanTest)_currentTest).failed();
	}

	
}