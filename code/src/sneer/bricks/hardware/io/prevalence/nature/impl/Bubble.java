package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Date;

import sneer.bricks.hardware.io.prevalence.map.ExportMap;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.ReadOnly;

class Bubble implements InvocationHandler {
	
	static CacheMap<Object, Object> _proxiesByDelegate = CacheMap.newInstance();

	
	static <T> T wrap(final T object) {
		return (T) _proxiesByDelegate.get(object, new Producer<Object>() { @Override public Object produce() {
			InvocationHandler handler = new Bubble(object);
			return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), handler);
		}});
	}
	
	
	private Bubble(Object delegate) {
		_delegate = delegate;
		checkExport();
	}

	
	private final Object _delegate;
	
	
	private void checkExport() {
		if (!my(ExportMap.class).isRegistered(_delegate))
			throw new IllegalStateException("Object '" + _delegate + "' was not ready to be exported from the prevalence environment. Use " + ExportMap.class.getSimpleName() + ".register(object).");
	}

	
	@Override
	public Object invoke(Object proxyImplied, Method method, Object[] args) throws Throwable {
		return method.getReturnType() == Void.TYPE
			? handleCommand(method, args)
			: handleQuery(method, args);
	}
	
	
	private Object handleCommand(Method method, Object[] args) {
		try {
			PrevaylerHolder._prevayler.execute(new Invocation(_delegate, method, args));
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
		return null;
	}

	
	private Object handleQuery(Method method, Object[] args) throws Throwable {
		Object result = invokeOnDelegate(method, args);
		return wrapIfNecessary(result, method);
	}

	
	private Object invokeOnDelegate(Method method, Object[] args) throws Throwable {
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
		if (object == null) return null;

		Class<?> type = method.getReturnType();
		if (isReadOnly(type)) return object;
		if (type.isArray()) return object;
		
		return wrap(object);
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
