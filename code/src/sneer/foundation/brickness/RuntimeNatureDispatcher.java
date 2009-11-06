package sneer.foundation.brickness;

import sneer.foundation.brickness.RuntimeNature.Continuation;

public interface RuntimeNatureDispatcher {
	
	Object dispatch(Class<?> brick, Object instance, String methodName, Object[] args, Continuation continuation);

}
