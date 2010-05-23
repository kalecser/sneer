package sneer.bricks.software.bricks.interception.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.List;

import org.apache.commons.lang.time.StopWatch;

import sneer.bricks.software.bricks.interception.InterceptionEnhancer;
import sneer.bricks.software.bricks.interception.tests.fixtures.combinedmethods.BrickOfSomeInterceptingNature;
import sneer.bricks.software.bricks.interception.tests.fixtures.nature.SomeInterceptingNature;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

public class ContinuationBenchmark {
	
	public static class NullRuntimeNature implements SomeInterceptingNature {

		@Override
		public Object invoke(Class<?> brick, Object instance,
				String methodName, Object[] args, Continuation continuation) {
			return continuation.invoke(args);
			
		}
		
		@Override
		public <T> T instantiate(Class<T> brick, Class<T> implClass,
				Producer<T> producer) {
			return producer.produce();
		}
		
		@Override
		public List<ClassDefinition> realize(Class<?> brick, ClassDefinition classDef) {
			return my(InterceptionEnhancer.class).realize(brick, SomeInterceptingNature.class, classDef);
		}
		
	}

	public static class ReflectionRuntimeNature extends NullRuntimeNature {

		@Override
		public Object invoke(Class<?> brick, Object instance,
				String methodName, Object[] args, Continuation continuation) {
			try {
				return instance.getClass().getDeclaredMethod("$" + methodName, parameterTypesFrom(args)).invoke(instance, args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private Class<?>[] parameterTypesFrom(Object[] args) {
			Class<?>[] classes = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++)
				classes[i] = toPrimitiveType(args[i].getClass());
			return classes;
		}

		private static Class<?> toPrimitiveType(Class<? extends Object> c) {
			if (c == Integer.class)
				return Integer.TYPE;
			return c;
		}

	}

	public static void main(String[] args) {
		for (int i=0; i<3; ++i) {
			benchmark("static: ", new NullRuntimeNature());
			benchmark("reflection: ", new ReflectionRuntimeNature());
		}
	}

	private static void benchmark(String label, SomeInterceptingNature runtimeNature) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		Environments.runWith(Brickness.newBrickContainer(runtimeNature), new Closure() { @Override public void run() {
			BrickOfSomeInterceptingNature brick = my(BrickOfSomeInterceptingNature.class);
			for (int i = 0; i < 1000000; ++i)
				brick.add(1, 2);
		}});
		
		stopWatch.stop();
		System.out.println(label + stopWatch.getTime());
	}

}
