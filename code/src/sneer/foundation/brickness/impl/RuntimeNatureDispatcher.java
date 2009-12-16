package sneer.foundation.brickness.impl;

import sneer.bricks.software.bricks.interception.Interceptor;
import sneer.bricks.software.bricks.interception.Interceptor.Continuation;

public class RuntimeNatureDispatcher {

	public static Object dispatch(Class<?> brick,
			Interceptor natures,
			Object instance,
			String methodName,
			Object[] args, Continuation continuation) {
		
		return natures.invoke(brick, instance, methodName, args, continuation);
	}

}
