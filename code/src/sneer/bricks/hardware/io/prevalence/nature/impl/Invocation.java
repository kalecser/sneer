package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.List;

import sneer.bricks.hardware.io.prevalence.map.ExportMap;
import sneer.foundation.lang.Immutable;

class Invocation extends BuildingTransaction<Object> {
		
	static void preApprove(Object object) {
		if (!my(ExportMap.class).isRegistered(object))
			throw new IllegalStateException("Object '" + object + "' was not ready to be exported from the prevalence environment. Use " + ExportMap.class.getSimpleName() + ".register(object).");
	}

	
	Invocation(Object object, Method method, Object[] args, List<String> getterPath) {
		_id = my(ExportMap.class).marshal(object);
		_methodName = method.getName();
		_getterPath = getterPath.toArray(EMPTY_STRING_ARRAY);
		_argTypes = method.getParameterTypes();	
		my(ExportMap.class).marshal(args);
		_args = args;	
	}


	private final long _id;
	private final String _methodName;
	private final String[] _getterPath;
	private final Class<?>[] _argTypes;
	private final Object[] _args;
	
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];	
	
	final static ExportMap _exportMap = my(ExportMap.class);
	
	@Override
	public Object produce() {
		
		Object receiver = navigateToReceiver(_exportMap.unmarshal(_id));
		_exportMap.unmarshal(_args);
		Object result = invoke(receiver, _methodName, _argTypes, _args);
		if (requiresRegistration(result))
			_exportMap.register(result);
		return result;
	}


	private boolean requiresRegistration(Object result) {
		if (result == null) return false;
		
		Class<?> type = result.getClass();
		if (type.isPrimitive()) return false;
		if (type == String.class) return false;
		if (type == Date.class) return false;
		if (type == File.class) return false;
		if (Immutable.class.isAssignableFrom(type)) return false;
		
		return !_exportMap.isRegistered(result);
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
		for (int i = 0; i < _getterPath.length; i++)
			result = invoke(result, _getterPath[i], EMPTY_CLASS_ARRAY);
		return result;
	}

}
