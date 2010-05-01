package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Method;
import java.util.List;

import sneer.bricks.hardware.io.prevalence.map.ExportMap;

class InvocationChain extends BuildingTransaction<Object> {
		
	InvocationChain(Object startObject, List<Method> methodPath, List<Object[]> argsPath) {
		_id = my(ExportMap.class).marshal(startObject);
		_methodPath = toMethodNames(methodPath);
		_argsPath = marshal(argsPath);
		_argsTypesPath = toArgsTypes(methodPath);
	}


	private final long _id;
	private final String[] _methodPath;
	private final Object[][] _argsPath;
	private final Class<?>[][] _argsTypesPath;
	
	
	@Override
	public Object produce() {
		Object result = my(ExportMap.class).unmarshal(_id);
		for (int i = 0; i < _methodPath.length; i++) {
			Object[] args = _argsPath[i]; 
			my(ExportMap.class).unmarshal(args);
			result = invoke(result, _methodPath[i], _argsTypesPath[i], args);
		}
		return result;
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
	
	
	private String[] toMethodNames(List<Method> queryPath) {
		String[] result = new String[queryPath.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = queryPath.get(i).getName();
		return result;
	}

	
	private Class<?>[][] toArgsTypes(List<Method> methodPath) {
		Class<?>[][] result = new Class<?>[methodPath.size()][];
		for (int i = 0; i < result.length; i++)
			result[i] = methodPath.get(i).getParameterTypes();
		return result;
	}


	private Object[][] marshal(List<Object[]> argsPath) {
		Object[][] result = new Object[argsPath.size()][];
		for (int i = 0; i < result.length; i++) {
			result[i] = argsPath.get(i);
			my(ExportMap.class).marshal(result[i]);
		}
		return result;
	}

}
