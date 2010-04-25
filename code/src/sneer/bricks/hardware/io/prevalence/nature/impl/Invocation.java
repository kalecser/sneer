package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Method;

import sneer.bricks.hardware.io.prevalence.map.ExportMap;

class Invocation extends BuildingTransaction<Object> {
		
	Invocation(long id, Method method, Object[] args) {
		_id = id;
		_methodName = method.getName();
		_argTypes = method.getParameterTypes();	
		_args = args;	
	}


	private final long _id;
	private final String _methodName;
	private final Class<?>[] _argTypes;
	private final Object[] _args;
	
	
	@Override
	public Object produce() {
		Object receiver = my(ExportMap.class).objectById(_id);
		return invoke(receiver, _methodName, _argTypes, Bubble.unmap(_args));
	}

	
	private Object invoke(Object receiver, String methodName, Class<?>[] argTypes, Object... args) {
		try {
			Method method = receiver.getClass().getMethod(methodName, argTypes);
			method.setAccessible(true);
			return method.invoke(receiver, args);
		} catch (Exception e) {
			throw new IllegalStateException("Exception trying to invoke " + receiver.getClass() + "." + methodName, e);
		}
	}

}
