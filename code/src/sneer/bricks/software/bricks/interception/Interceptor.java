package sneer.bricks.software.bricks.interception;

public interface Interceptor {
	
	public interface Continuation {
		Object invoke(Object[] args);
	}
	
	Object invoke(Class<?> brick, Object instance, String methodName, Object[] args, Continuation continuation);
	
}
