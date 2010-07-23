package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import sneer.bricks.hardware.io.prevalence.map.PrevalenceMap;
import sneer.foundation.lang.Producer;

class Invocation implements Producer<Object> {

	protected static final PrevalenceMap PrevalenceMap = my(PrevalenceMap.class);

	
	Invocation(Producer<Object> targetProducer, Method method, Object[] args) {
		_targetProducer = targetProducer;
		_method = method.getName();
		_argsTypes = method.getParameterTypes();
		_args = marshal(args);
	}


	private final Producer<Object> _targetProducer;
	private final String _method;
	private final Class<?>[] _argsTypes;
	private final Object[] _args;
	
	
	@Override
	public Object produce() {
		Object target = _targetProducer.produce();
		return invoke(target, _method, _argsTypes, unmarshal(_args));
	}


	static private Object invoke(Object receiver, String methodName, Class<?>[] argTypes, Object... args) {
		try {
			Method method = receiver.getClass().getMethod(methodName, argTypes);
			method.setAccessible(true);
			return method.invoke(receiver, args);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getTargetException();
			if (cause instanceof RuntimeException)
				throw (RuntimeException)cause;
			throw new IllegalStateException("Exception trying to invoke " + receiver.getClass() + "." + methodName, cause);
		} catch (Exception e) {
			throw new IllegalStateException("Exception trying to invoke " + receiver.getClass() + "." + methodName, e);
		}
	}

	
	static private Object[] unmarshal(Object[] args) {
		return PrevalenceMap.unmarshal(args);
	}

	
	static private Object[] marshal(Object[] args) {
		return PrevalenceMap.marshal(args);
	}

}
