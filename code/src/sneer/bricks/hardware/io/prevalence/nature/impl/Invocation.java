package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.Arrays;

import sneer.bricks.hardware.io.prevalence.map.ExportMap;
import sneer.foundation.lang.Immutable;

class Invocation extends BuildingTransaction<Object> {
		
	private static final ExportMap ExportMap = my(ExportMap.class);



	Invocation(Object delegate) {
		_id = ExportMap.marshal(delegate);

		_previous = null;
		_method = null;
		_argsTypes = null;
		_args = null;
	}


	Invocation(Invocation previous, Method method, Object[] args) {
		_id = -1;
		
		_previous = previous;
		_method = method.getName();
		_argsTypes = method.getParameterTypes();
		_args = marshal(args);
	}


	private final long _id;
	
	private final Invocation _previous;
	private final String _method;
	private final Class<?>[] _argsTypes;
	private final Object[] _args;
	
	final static ExportMap _exportMap = my(ExportMap.class);
	
	@Override
	public Object produce() {
		Object result = produceFromStart();
		registerIfNecessary(result);
		return result;
	}

	private Object produceFromStart() {
		if (_previous == null)
			return ExportMap.unmarshal(_id);
		
		Object previousResult = _previous.produceFromStart();
		ExportMap.unmarshal(_args);
		return invoke(previousResult, _method, _argsTypes, _args);
	}


	static private Object invoke(Object receiver, String methodName, Class<?>[] argTypes, Object... args) {
		try {
			Method method = receiver.getClass().getMethod(methodName, argTypes);
			method.setAccessible(true);
			return method.invoke(receiver, args);
		} catch (Exception e) {
			throw new IllegalStateException("Exception trying to invoke " + receiver.getClass() + "." + methodName, e);
		}
	}

	
	static private void registerIfNecessary(Object result) {
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

	
	static private Object[] marshal(Object[] args) {
		if (args == null) return null;
		Object[] result = Arrays.copyOf(args, args.length);
		ExportMap.marshal(result);
		return result;
	}

}
