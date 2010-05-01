package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.io.prevalence.map.ExportMap;

class Invocation extends BuildingTransaction<Object> {
		
	Invocation(Object object, Method method, Object[] args, List<Method> queryPath) {
		_id = my(ExportMap.class).marshal(object);
		_methodName = method.getName();
		_queryPath = toMethodNames(queryPath);
		_argTypes = method.getParameterTypes();	
		my(ExportMap.class).marshal(args);
		_args = args;	
	}


	private final long _id;
	private final String _methodName;
	private final String[] _queryPath;
	private final Class<?>[] _argTypes;
	private final Object[] _args;
	
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];	
	
	
	@Override
	public Object produce() {
		Object receiver = navigateToReceiver(my(ExportMap.class).unmarshal(_id));
		my(ExportMap.class).unmarshal(_args);
		return invoke(receiver, _methodName, _argTypes, _args);
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
	
	private Object navigateToReceiver(Object brick) {
		Object result = brick;
		for (int i = 0; i < _queryPath.length; i++)
			result = invoke(result, _queryPath[i], EMPTY_CLASS_ARRAY);
		return result;
	}

	private String[] toMethodNames(List<Method> queryPath) {
		ArrayList<String> getterPath = new ArrayList<String>(queryPath.size());
		for (Method m : queryPath) getterPath.add(m.getName());
		
		return getterPath.toArray(EMPTY_STRING_ARRAY);
	}

}
