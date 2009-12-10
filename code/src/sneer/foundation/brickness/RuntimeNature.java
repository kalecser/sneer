package sneer.foundation.brickness;

import sneer.foundation.lang.Producer;

public interface RuntimeNature extends Nature {
	
	public interface Continuation {
		Object invoke(Object[] args);
	}
	
	<T> T instantiate(Class<T> brick, Class<?> implClass, Producer<T> producer);
	
	Object invoke(Class<?> brick, Object instance, String methodName, Object[] args, Continuation continuation);
	
}
