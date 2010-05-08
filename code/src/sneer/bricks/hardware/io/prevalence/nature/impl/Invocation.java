package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Method;
import java.util.Arrays;

import sneer.bricks.hardware.io.prevalence.map.PrevalenceMap;
import sneer.foundation.lang.Immutable;

class Invocation extends BuildingTransaction<Object> {
		
	private static final PrevalenceMap PrevalenceMap = my(PrevalenceMap.class);



	Invocation(Object delegate) {
		_id = PrevalenceMap.marshal(delegate);

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
	
	final static PrevalenceMap _exportMap = my(PrevalenceMap.class);
	
	@Override
	public Object produce() {
		Object result = produceFromStart();
		registerIfNecessary(result);
		return result;
	}

	private Object produceFromStart() {
		if (_previous == null)
			return PrevalenceMap.unmarshal(_id);
		
		Object previousResult = _previous.produceFromStart();
		PrevalenceMap.unmarshal(_args);
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
		if (Immutable.isImmutable(type)) return;
		
		if (PrevalenceMap.isRegistered(result)) return;
		
		PrevalenceMap.register(result);
	}

	
	static private Object[] marshal(Object[] args) {
		if (args == null) return null;
		Object[] result = Arrays.copyOf(args, args.length);
		PrevalenceMap.marshal(result);
		return result;
	}

}
