package sneer.foundation.brickness.impl.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Assert;
import org.junit.Test;

import sneer.foundation.brickness.Brickness;
import sneer.foundation.brickness.RuntimeNature;
import sneer.foundation.brickness.RuntimeNature.Continuation;
import sneer.foundation.brickness.impl.tests.fixtures.runtimenature.brick.BrickOfSomeRuntimeNature;
import sneer.foundation.brickness.impl.tests.fixtures.runtimenature.nature.SomeRuntimeNature;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;

public class RuntimeNatureTest extends Assert {
	
	final Mockery mockery = new JUnit4Mockery();
	
	final SomeRuntimeNature runtimeNatureMock = mockery.mock(SomeRuntimeNature.class);
	
	@Test
	public void runtimeNatureInterceptsInvocations() {
		checking("foo", new Object[0], null, new Runnable() { @Override public void run() {
			my(BrickOfSomeRuntimeNature.class).foo();
		}});
	}
	
	@Test
	public void referenceReturnValue() {
		checking("bar", new Object[0], "42", new Runnable() { @Override public void run() {
			assertEquals("42", my(BrickOfSomeRuntimeNature.class).bar());
		}});
	}
	
	@Test
	public void primitiveReturnValue() {
		checking("baz", new Object[0], 42, new Runnable() { @Override public void run() {
			assertEquals(42, my(BrickOfSomeRuntimeNature.class).baz());
		}});
	}
	
	@Test
	public void referenceParameters() {
		checking("foo", new Object[] { "42" }, null, new Runnable() { @Override public void run() {
			my(BrickOfSomeRuntimeNature.class).foo("42");
		}});
	}
	
	@Test
	public void primitiveParameters() {
		checking("add", new Object[] { 1, 2 }, 3, new Runnable() { @Override public void run() {
			my(BrickOfSomeRuntimeNature.class).add(1, 2);
		}});
	}
	
	@Test
	public void continuationWithParameters() {
		
		mockery.checking(new Expectations() {{
			oneOf(runtimeNatureMock).invoke(
					with(BrickOfSomeRuntimeNature.class),
					with(any(BrickOfSomeRuntimeNature.class)),
					with("add"),
					with(new Object[] { 1, 2 }),
					with(any(RuntimeNature.Continuation.class)));
			will(new CustomAction("validate continuation") { @Override public Object invoke(Invocation invocation) throws Throwable {
				RuntimeNature.Continuation continuation = (Continuation) invocation.getParameter(4);
				Object returnValue = continuation.invoke(new Object[] { 1, 2 });
				assertEquals(3, returnValue);
				return returnValue;
			}});
		}});
		
		Environment subject = Brickness.newBrickContainer(runtimeNatureMock);
		Environments.runWith(subject, new Runnable() { @Override public void run() {
			my(BrickOfSomeRuntimeNature.class).add(1, 2);
		}});
		mockery.assertIsSatisfied();
	}

	private void checking(final String expectedMethodName,
			final Object[] expectedArgs, final Object expectedReturnValue, Runnable invocationBlock) {
		mockery.checking(new Expectations() {{
			oneOf(runtimeNatureMock).invoke(
					with(BrickOfSomeRuntimeNature.class),
					with(any(BrickOfSomeRuntimeNature.class)),
					with(expectedMethodName),
					with(expectedArgs),
					with(any(RuntimeNature.Continuation.class)));
			will(returnValue(expectedReturnValue));
		}});
		
		Environment subject = Brickness.newBrickContainer(runtimeNatureMock);
		
		Environments.runWith(subject, invocationBlock);
		
		mockery.assertIsSatisfied();
	}

}
