package sneer.foundation.brickness;

public interface RuntimeNature extends Nature {
	
	public interface Continuation {
		Object invoke(Object[] args);
	}
	
	Object invoke(Class<?> brick, Object instance, String methodName, Object[] args, Continuation continuation);

}
