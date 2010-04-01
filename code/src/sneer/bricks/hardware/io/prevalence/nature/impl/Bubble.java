package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.prevayler.Prevayler;

import sneer.foundation.lang.ReadOnly;


class Bubble {

	static <BRICK> BRICK wrap(Prevayler prevayler, Class<BRICK> brick, BRICK brickImpl) {
		return wrap(brick, brickImpl, prevayler, new ArrayList<String>());
	}
	
	private static <T> T wrap(Class<?> brick, T object, Prevayler prevayler, List<String> getterMethodPath) {
		InvocationHandler handler = new Bubble(brick, object, prevayler, getterMethodPath).handler();
		return (T)Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), handler);
	}

	
	private Bubble(Class<?> brick, Object brickImpl, Prevayler prevayler, List<String> getterMethodPath) {
		_brick = brick;
		_brickImpl = brickImpl;
		_prevayler = prevayler;
		_getterMethodPath = getterMethodPath;
	}
	
	
	private final Class<?> _brick;
	private final Object _brickImpl;
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
			_prevayler.execute(new Invocation(_brick, _getterMethodPath, method, args));
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
			return method.invoke(_brickImpl, args);
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

		Class<?> type = method.getReturnType();
		if (isReadOnly(type)) return object;
		if (type.isArray()) return object;

		List<String> pathToObject = new ArrayList<String>(_getterMethodPath.size() + 1);
		pathToObject.addAll(_getterMethodPath);
		pathToObject.add(methodName);
		
		return wrap(_brick, object, _prevayler, pathToObject);
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
