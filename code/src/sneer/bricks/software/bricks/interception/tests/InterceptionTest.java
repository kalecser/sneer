package sneer.bricks.software.bricks.interception.tests;

import static sneer.foundation.environments.Environments.*;

import java.lang.reflect.Method;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.tests.BrickTestWithThreads;
import sneer.bricks.software.bricks.interception.InterceptionEnhancer;
import sneer.bricks.software.bricks.interception.Interceptor;
import sneer.bricks.software.bricks.interception.Interceptor.Continuation;
import sneer.bricks.software.bricks.interception.fixtures.brickwithlib.BrickWithLib;
import sneer.bricks.software.bricks.interception.fixtures.combinedmethods.BrickOfSomeInterceptingNature;
import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;
import sneer.bricks.software.bricks.interception.fixtures.primitivemethods.noargs.PrimitiveMethodNoArgs;
import sneer.bricks.software.bricks.interception.fixtures.refmethods.noargs.RefMethodsNoArgs;
import sneer.bricks.software.bricks.interception.fixtures.staticinitializer.StaticInitializer;
import sneer.bricks.software.bricks.interception.fixtures.voidmethods.noargs.VoidMethodsNoArgs;
import sneer.bricks.software.bricks.interception.fixtures.voidmethods.onearg.VoidMethodsRefArg;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.Producer;

public class InterceptionTest extends BrickTestWithThreads {
	
	final Mockery mockery = new JUnit4Mockery();
	
	final SomeInterceptingNature interceptingNatureMock = mockery.mock(SomeInterceptingNature.class);
	
	@Test
	public void voidMethodNoArgs() {
		checkingMethodIsInvoked(VoidMethodsNoArgs.class, "foo", new Object[0], new Closure() { @Override public void run() {
			my(VoidMethodsNoArgs.class).foo();
		}});
	}
	
	@Test
	public void existingStaticInitializer() {
		checkingMethodIsInvoked(StaticInitializer.class, "foo", new Object[0], new Closure() { @Override public void run() {
			my(StaticInitializer.class).foo();
		}});
	}
	
	@Test
	public void voidMethodRefArg() {
		checkingMethodIsInvoked(VoidMethodsRefArg.class, "foo", new Object[] { "42" }, new Closure() { @Override public void run() {
			my(VoidMethodsRefArg.class).foo("42");
		}});
	}
	
	
	@Test
	public void referenceReturnValue() {
		checkingMethodIsInvoked(RefMethodsNoArgs.class, "bar", new Object[0], new Closure() { @Override public void run() {
			assertEquals("42", my(RefMethodsNoArgs.class).bar());
		}});
	}
	
	
	@Test
	public void primitiveReturnValue() {
		checkingMethodIsInvoked(PrimitiveMethodNoArgs.class, "baz", new Object[0], new Closure() { @Override public void run() {
			assertEquals(42, my(PrimitiveMethodNoArgs.class).baz());
		}});
	}
	
	@Test
	public void primitiveParametersWithPrimitiveReturnValue() {
		checkingMethodIsInvoked("add", new Object[] { 1, 2 }, new Closure() { @Override public void run() {
			my(BrickOfSomeInterceptingNature.class).add(1, 2);
		}});
	}
	
	
	@Test
	public void overridenMethods() {
		SomeInterceptingNature passThroughNature = new SomeInterceptingNature() {
			
			@Override public Object invoke(Class<?> brick, Object instance, String methodName, Object[] args, Continuation continuation) {
				return continuation.invoke(args);
			}

			@Override
			public <T> T instantiate(Class<T> brick, Class<T> implClass, Producer<T> producer) {
				return producer.produce();
			}
			
			@Override
			public List<ClassDefinition> realize(Class<?> brick, ClassDefinition classDef) {
				return my(InterceptionEnhancer.class).realize(brick, SomeInterceptingNature.class, classDef);
			}
		};
			
		Environment newEnvironment = Brickness.newBrickContainer(passThroughNature);
		Environments.runWith(newEnvironment, new Closure() { @Override public void run() {
			assertEquals("Hello!!!", my(BrickOfSomeInterceptingNature.class).newGreeter().hello());
		}});
		crash(newEnvironment);
	}

	
	@Test
	public void environmentIsNotRequired() {
		checkingMethodIsInvoked(VoidMethodsNoArgs.class, "foo", new Object[0], new Closure() { @Override public void run() {
			final VoidMethodsNoArgs brick = my(VoidMethodsNoArgs.class);
			Environments.runWith(null, new Closure() { @Override public void run() {
				brick.foo();
			}});
		}});
	}
	
	
	@Test
	public void unaccessibleMethodsAreNotIntercepted() throws Exception {
		allowingRealizeAndInstantiate();
		
		runCheckingMockery(new ClosureX<Exception>() { @Override public void run() throws Exception {
			invokeMethod("privateMethod");
			invokeMethod("packageMethod");
		}});
	}

	
	@Test
	public void protectedMethodsAreIntercepted() throws Exception {
		checkingMethodIsInvoked("protectedMethod", new Object[0], new ClosureX<Exception>() { @Override public void run() throws Exception {
			invokeMethod("protectedMethod");
		}});
	}
	
	
	@Test
	public void continuationWithParameters() {
		allowingRealizeAndInstantiate();
			
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
		
		runCheckingMockery(new Closure() { @Override public void run() {
			my(BrickOfSomeInterceptingNature.class).add(1, 2);
		}});
	}

	
	@Test
	public void brickWithLib() throws Exception {
		checkingMethodIsInvoked(BrickWithLib.class, "fooBar", new Object[0], new Closure() { @Override public void run() {
			mockery.checking(new Expectations() {{
				
				oneOf(interceptingNatureMock).invoke(
						with(BrickWithLib.class),
						with(any(BrickWithLib.class)),
						with("useLibType"),
						with(any(Object[].class)),
						with(any(Interceptor.Continuation.class)));
				will(returnValue(42));
			}});
			
			my(BrickWithLib.class).fooBar();
		}});
	}

	
	private void invokeMethod(final String methodName) throws Exception {
		BrickOfSomeInterceptingNature brick = my(BrickOfSomeInterceptingNature.class);
		Method method = brick.getClass().getDeclaredMethod(methodName);
		method.setAccessible(true);
		method.invoke(brick);
	}
	
	
	private <X extends Exception> void runCheckingMockery(ClosureX<X> block) throws X {
		Environment subject = Brickness.newBrickContainer(interceptingNatureMock);
		Environments.runWith(subject, block);
		mockery.assertIsSatisfied();
		crash(subject);
	}
	
	
	private void allowingRealizeAndInstantiate() {
		allowingRealizeAndInstantiate(BrickOfSomeInterceptingNature.class);
	}

	
	private void allowingRealizeAndInstantiate(final Class<?> brickClass) {
		mockery.checking(new Expectations() {{
			allowing(interceptingNatureMock).realize(with(brickClass), with(any(ClassDefinition.class)));
			will(new CustomAction("realize") { @Override public Object invoke(Invocation invocation) throws Throwable {
				ClassDefinition classDef = (ClassDefinition) invocation.getParameter(1);
				return my(InterceptionEnhancer.class).realize(brickClass, SomeInterceptingNature.class, classDef);
			}});
			
			oneOf(interceptingNatureMock).instantiate(
					with(brickClass),
					with(any(Class.class)),
					with(any(Producer.class)));
			
			will(new CustomAction("producer") { @Override public Object invoke(Invocation invocation) throws Throwable {
				Producer<?> producer = (Producer<?>) invocation.getParameter(2);
				return producer.produce();
			}});
		}});
	}

	
	private  <X extends Exception> void checkingMethodIsInvoked(
			final String expectedMethodName,
			final Object[] expectedArgs,
			ClosureX<X> invocationBlock) throws X {
		
		checkingMethodIsInvoked(BrickOfSomeInterceptingNature.class,
				expectedMethodName,
				expectedArgs,
				invocationBlock);
	}

	
	private <X extends Exception> void checkingMethodIsInvoked(
			final Class<?> brick,
			final String expectedMethodName, final Object[] expectedArgs,
			ClosureX<X> invocationBlock) throws X {
		
		allowingRealizeAndInstantiate(brick);
		
		mockery.checking(new Expectations() {{
			
			oneOf(interceptingNatureMock).invoke(
					with(brick),
					with(any(brick)),
					with(expectedMethodName),
					with(expectedArgs),
					with(any(Interceptor.Continuation.class)));
			will(new CustomAction("continuation") { @Override public Object invoke(Invocation invocation) throws Throwable {
				Interceptor.Continuation continuation = (Interceptor.Continuation) invocation.getParameter(4);
				return continuation.invoke(expectedArgs);
			}});
		}});
		
		runCheckingMockery(invocationBlock);
	}

}
