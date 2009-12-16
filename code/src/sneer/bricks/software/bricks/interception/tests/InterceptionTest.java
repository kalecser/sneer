package sneer.bricks.software.bricks.interception.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Assert;
import org.junit.Test;

import sneer.bricks.software.bricks.interception.InterceptionEnhancer;
import sneer.bricks.software.bricks.interception.Interceptor;
import sneer.bricks.software.bricks.interception.Interceptor.Continuation;
import sneer.bricks.software.bricks.interception.tests.fixtures.brick.BrickOfSomeInterceptingNature;
import sneer.bricks.software.bricks.interception.tests.fixtures.nature.SomeInterceptingNature;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Producer;

public class InterceptionTest extends Assert {
	
	final Mockery mockery = new JUnit4Mockery();
	
	final SomeInterceptingNature interceptingNatureMock = mockery.mock(SomeInterceptingNature.class);
	
	@Test
	public void runtimeNatureInterceptsInvocations() {
		checking("foo", new Object[0], null, new Runnable() { @Override public void run() {
			my(BrickOfSomeInterceptingNature.class).foo();
		}});
	}
	
	@Test
	public void referenceReturnValue() {
		checking("bar", new Object[0], "42", new Runnable() { @Override public void run() {
			assertEquals("42", my(BrickOfSomeInterceptingNature.class).bar());
		}});
	}
	
	@Test
	public void primitiveReturnValue() {
		checking("baz", new Object[0], 42, new Runnable() { @Override public void run() {
			assertEquals(42, my(BrickOfSomeInterceptingNature.class).baz());
		}});
	}
	
	@Test
	public void referenceParameters() {
		checking("foo", new Object[] { "42" }, null, new Runnable() { @Override public void run() {
			my(BrickOfSomeInterceptingNature.class).foo("42");
		}});
	}
	
	@Test
	public void primitiveParameters() {
		checking("add", new Object[] { 1, 2 }, 3, new Runnable() { @Override public void run() {
			my(BrickOfSomeInterceptingNature.class).add(1, 2);
		}});
	}
	
	@Test
	public void overridenMethods() {
		SomeInterceptingNature passThroughNature = new SomeInterceptingNature() { @Override public Object invoke(Class<?> brick, Object instance, String methodName, Object[] args, Continuation continuation) {
			return continuation.invoke(args);
		}

		@Override
		public <T> T instantiate(Class<T> brick, Class<?> implClass,
				Producer<T> producer) {
			return producer.produce();
		}
		
		@Override
		public List<ClassDefinition> realize(ClassDefinition classDef) {
			return my(InterceptionEnhancer.class).realize(SomeInterceptingNature.class, classDef);
		}};
		
		
		Environments.runWith(Brickness.newBrickContainer(passThroughNature), new Runnable() { @Override public void run() {
			assertEquals("Hello!!!", my(BrickOfSomeInterceptingNature.class).newGreeter().hello());
		}});
	}
	
	@Test
	public void environmentIsNotRequired() {
		checking("add", new Object[] { 1, 2 }, 3, new Runnable() { @Override public void run() {
			final BrickOfSomeInterceptingNature brick = my(BrickOfSomeInterceptingNature.class);
			Environments.runWith(null, new Runnable() { @Override public void run() {
				brick.add(1, 2);
			}});
		}});
	}
	
	@Test
	public void continuationWithParameters() {
		
		checkingRealizeInstantiate();
			
		mockery.checking(new Expectations() {{
				
			oneOf(interceptingNatureMock).invoke(
					with(BrickOfSomeInterceptingNature.class),
					with(any(BrickOfSomeInterceptingNature.class)),
					with("add"),
					with(new Object[] { 1, 2 }),
					with(any(Interceptor.Continuation.class)));
			
			will(new CustomAction("validate continuation") { @Override public Object invoke(Invocation invocation) throws Throwable {
				Interceptor.Continuation continuation = (Continuation) invocation.getParameter(4);
				Object returnValue = continuation.invoke(new Object[] { 1, 2 });
				assertEquals(3, returnValue);
				return returnValue;
			}});
		}});
		
		Environment subject = Brickness.newBrickContainer(interceptingNatureMock);
		Environments.runWith(subject, new Runnable() { @Override public void run() {
			my(BrickOfSomeInterceptingNature.class).add(1, 2);
		}});
		mockery.assertIsSatisfied();
	}
	
	private void checkingRealizeInstantiate() {
		mockery.checking(new Expectations() {{
			
			oneOf(interceptingNatureMock).realize(with(any(ClassDefinition.class)));
			will(new CustomAction("realize") { @Override public Object invoke(Invocation invocation) throws Throwable {
				ClassDefinition classDef = (ClassDefinition) invocation.getParameter(0);
				return my(InterceptionEnhancer.class).realize(SomeInterceptingNature.class, classDef);
			}});
			
			oneOf(interceptingNatureMock).instantiate(
					with(BrickOfSomeInterceptingNature.class),
					with(any(Class.class)),
					with(any(Producer.class)));
			
			will(new CustomAction("producer") { @Override public Object invoke(Invocation invocation) throws Throwable {
				Producer<?> producer = (Producer<?>) invocation.getParameter(2);
				return producer.produce();
			}});
			
		}});
	}

	private void checking(final String expectedMethodName,
			final Object[] expectedArgs, final Object expectedReturnValue, Runnable invocationBlock) {
		
		checkingRealizeInstantiate();
		
		mockery.checking(new Expectations() {{
			
			oneOf(interceptingNatureMock).invoke(
					with(BrickOfSomeInterceptingNature.class),
					with(any(BrickOfSomeInterceptingNature.class)),
					with(expectedMethodName),
					with(expectedArgs),
					with(any(Interceptor.Continuation.class)));
			will(returnValue(expectedReturnValue));
		}});
		
		Environment subject = Brickness.newBrickContainer(interceptingNatureMock);
		
		Environments.runWith(subject, invocationBlock);
		
		mockery.assertIsSatisfied();
	}

}
