package sneer.foundation.brickness.impl.tests;

import static sneer.foundation.environments.Environments.my;

import org.apache.commons.lang.time.StopWatch;

import sneer.foundation.brickness.Brickness;
import sneer.foundation.brickness.impl.tests.fixtures.runtimenature.brick.BrickOfSomeRuntimeNature;
import sneer.foundation.brickness.impl.tests.fixtures.runtimenature.nature.SomeRuntimeNature;
import sneer.foundation.environments.Environments;

public class ContinuationBenchmark {
	
	public static class NullRuntimeNature implements SomeRuntimeNature {

		@Override
		public Object invoke(Class<?> brick, Object instance,
				String methodName, Object[] args, Continuation continuation) {
			return continuation.invoke(args);
			
		}
	}

	public static class ReflectionRuntimeNature implements SomeRuntimeNature {

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

	private static void benchmark(String label,
			SomeRuntimeNature runtimeNature) {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		Environments.runWith(Brickness.newBrickContainer(runtimeNature), new Runnable() {
			@Override
			public void run() {
				
				BrickOfSomeRuntimeNature brick = my(BrickOfSomeRuntimeNature.class);
				for (int i=0; i<1000000; ++i)
					brick.add(1, 2);

			}
		});
		
		stopWatch.stop();
		System.out.println(label + stopWatch.getTime());
		
	}

}
