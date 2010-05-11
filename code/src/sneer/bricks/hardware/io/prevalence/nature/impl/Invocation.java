package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.lang.reflect.Method;
import java.util.Arrays;

class Invocation extends BuildingTransaction {

	
	Invocation(BuildingTransaction previous, Method method, Object[] args) {
		_previous = previous;
		_method = method.getName();
		_argsTypes = method.getParameterTypes();
		_args = marshal(args);
	}


	private final BuildingTransaction _previous;
	private final String _method;
	private final Class<?>[] _argsTypes;
	private final Object[] _args;
	
	
	@Override
	protected Object execute() {
		Object previousResult = _previous.execute();
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

	
	static private Object[] marshal(Object[] args) {
		if (args == null) return null;
		Object[] result = Arrays.copyOf(args, args.length);
		PrevalenceMap.marshal(result);
		return result;
	}

}
