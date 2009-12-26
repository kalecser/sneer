package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.prevayler.Prevayler;


class Bubble {

	static <STATE_MACHINE> STATE_MACHINE wrapStateMachine(Prevayler prevayler) {
		Object stateMachine = prevayler.prevalentSystem();
		return (STATE_MACHINE)wrap(stateMachine, prevayler, new ArrayList<String>());
	}

	
	private static <T> T wrap(Object object, Prevayler prevayler, List<String> getterMethodPath) {
		InvocationHandler handler = new Bubble(object, prevayler, getterMethodPath).handler();
		return (T)Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), handler);
	}

	
	private Bubble(Object stateMachine, Prevayler prevayler, List<String> getterMethodPath) {
		_stateMachine = stateMachine;
		_prevayler = prevayler;
		_getterMethodPath = getterMethodPath;
	}
	
	
	private final Object _stateMachine;
	private final Prevayler _prevayler;
	private final List<String> _getterMethodPath;
	
	
	private InvocationHandler handler() {
		return new InvocationHandler() { @Override public Object invoke(Object proxyImplied, Method method, Object[] args) throws Throwable {
			return method.getReturnType() == Void.TYPE
				? handleCommand(method, args)
				: handleQuery(method, args);
		}};
	}
	
	
	private Object handleCommand(Method method, Object[] args) {
		try {
			_prevayler.execute(new Invocation(_getterMethodPath, method, args));
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
		return null;
	}


	private Object handleQuery(Method method, Object[] args) throws Throwable {
		Object result = invokeOnStateMachine(method, args);
		return wrapIfNecessary(result, method);
	}


	private Object invokeOnStateMachine(Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(_stateMachine, args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}


	private Object wrapIfNecessary(Object object, Method method) {
		String methodName = method.getName();
		if (methodName.equals("output")) return object;
		
		Class<?> type = method.getReturnType();
		if (isPrimitive(type)) return object;

		List<String> pathToObject = new ArrayList<String>(_getterMethodPath.size() + 1);
		pathToObject.addAll(_getterMethodPath);
		pathToObject.add(methodName);
		
		return wrap(object, _prevayler, pathToObject);
	}


	private boolean isPrimitive(Class<?> type) {
		if (type.isPrimitive()) return true;
		if (type == String.class) return true;
		if (type == Date.class) return true;
		return false;
	}

}
