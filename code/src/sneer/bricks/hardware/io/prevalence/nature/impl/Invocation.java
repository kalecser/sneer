package sneer.bricks.hardware.io.prevalence.nature.impl;

import static basis.environments.Environments.my;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import basis.lang.ProducerX;

import sneer.bricks.hardware.io.prevalence.map.PrevalenceMap;

class Invocation implements ProducerX<Object, Exception> {

	protected static final PrevalenceMap PrevalenceMap = my(PrevalenceMap.class);

	
	Invocation(ProducerX<Object, ? extends Exception> targetProducer, Method method, Object[] args) {
		_targetProducer = targetProducer;
		_method = method.getName();
		_argsTypes = method.getParameterTypes();
		_args = marshal(args);
	}


	private final ProducerX<Object, ? extends Exception> _targetProducer;
	private final String _method;
	private final Class<?>[] _argsTypes;
	private final Object[] _args;
	
	
	@Override
	public Object produce() throws Exception {
		Object target = _targetProducer.produce();
		return invoke(target, _method, _argsTypes, unmarshal(_args));
	}


	static private Object invoke(Object receiver, String methodName, Class<?>[] argTypes, Object... args) throws Exception {
		try {
			Method method = receiver.getClass().getMethod(methodName, argTypes);
			method.setAccessible(true);
			return method.invoke(receiver, args);
		} catch (InvocationTargetException e) {
			Throwable throwable = e.getTargetException();
			if (throwable instanceof Error) throw (Error)throwable;
			if (throwable instanceof Exception) throw (Exception)throwable;
			throw new IllegalStateException("Throwable thrown by " + receiver.getClass() + "." + methodName, throwable);
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
