package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Date;

import org.prevayler.Prevayler;

import sneer.foundation.lang.ReadOnly;


class Bubble {

	static <T> T wrap(T object, Prevayler prevayler) {
		InvocationHandler handler = new Bubble(object, prevayler).handler();
		return (T)Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), handler);
	}

	
	private Bubble(Object delegate, Prevayler prevayler) {
		_delegate = delegate;
		_prevayler = prevayler;
	}
	
	
	private final Object _delegate;
	private final Prevayler _prevayler;
		
	private InvocationHandler handler() {
		return new InvocationHandler() { @Override public Object invoke(Object proxyImplied, Method method, Object[] args) throws Throwable {
			return method.getReturnType() == Void.TYPE
				? handleCommand(method, args)
				: handleQuery(method, args);
		}};
	}
	
	
	private Object handleCommand(Method method, Object[] args) {
		try {
			_prevayler.execute(new Invocation(idFor(_delegate), method, map(args)));
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
		return null;
	}

	private Object[] map(Object[] args) {
		return args;
	}

	private long idFor(Object object) {
		return ((PrevalentBuilding)_prevayler.prevalentSystem()).idFor(object);
	}

	private Object handleQuery(Method method, Object[] args) throws Throwable {
		Object result = invokeOnStateMachine(method, args);
		return wrapIfNecessary(result, method);
	}

	private Object invokeOnStateMachine(Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(_delegate, args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}


	private Object wrapIfNecessary(Object object, Method method) {

		Class<?> type = method.getReturnType();
		if (isReadOnly(type)) return object;
		if (type.isArray()) return object;
		
		return wrap(object, _prevayler);
	}


	private boolean isReadOnly(Class<?> type) {
		if (type.isPrimitive()) return true;
		if (type == String.class) return true;
		if (type == Date.class) return true;
		if (type == File.class) return true;
		if (ReadOnly.class.isAssignableFrom(type)) return true;

		return false;
	}

}
