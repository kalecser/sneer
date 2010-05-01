package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.List;

import sneer.bricks.hardware.io.prevalence.map.ExportMap;
import sneer.foundation.lang.Immutable;

class InvocationChain extends BuildingTransaction<Object> {
		
	private static final ExportMap ExportMap = my(ExportMap.class);


	InvocationChain(Object startObject, List<Method> methodPath, List<Object[]> argsPath) {
		_id = ExportMap.marshal(startObject);
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
		Object result = invokeMethodPath();
		registerIfNecessary(result);
		return result;
	}


	private void registerIfNecessary(Object result) {
		if (result == null) return;
		
		Class<?> type = result.getClass();
		if (type.isPrimitive()) return;
		if (type == String.class) return;
		if (type == Date.class) return;
		if (type == File.class) return;
		if (Immutable.class.isAssignableFrom(type)) return;
		
		if (ExportMap.isRegistered(result)) return;
		
		ExportMap.register(result);
	}


	private Object invokeMethodPath() {
		Object result = ExportMap.unmarshal(_id);
		for (int i = 0; i < _methodPath.length; i++) {
			Object[] args = _argsPath[i]; 
			ExportMap.unmarshal(args);
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
			ExportMap.marshal(result[i]);
		}
		return result;
	}

}
