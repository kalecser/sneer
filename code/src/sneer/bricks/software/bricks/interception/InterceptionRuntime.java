package sneer.bricks.software.bricks.interception;

public class InterceptionRuntime {

	public static Object dispatch(
			Class<?> brick,
			Interceptor interceptor,
			Object instance,
			String methodName,
			Object[] args,
			Interceptor.Continuation continuation) {
		
		return interceptor.invoke(brick, instance, methodName, args, continuation);
	}

}
