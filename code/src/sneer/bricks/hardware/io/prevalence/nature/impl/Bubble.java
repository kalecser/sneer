package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Date;

import sneer.bricks.hardware.io.prevalence.nature.Transaction;
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
		Invocation.preApprove(_delegate);
	}

	
	private final Object _delegate;
	
	
	@Override
	public Object invoke(Object proxyImplied, Method method, Object[] args) throws Throwable {
		Object result = isTransaction(method)
			? handleTransaction(method, args)
			: handleQuery(method, args);

		return wrapIfNecessary(result, method);
	}


	private boolean isTransaction(Method method) {
		if (method.getReturnType() == Void.TYPE) return true;
		if (method.getAnnotation(Transaction.class) != null) return true;
		return false;
	}
	
	
	private Object handleTransaction(Method method, Object[] args) {
		try {
			return PrevaylerHolder._prevayler.execute(new Invocation(_delegate, method, args));
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	
	private Object handleQuery(Method method, Object[] args) throws Throwable {
		return invokeOnDelegate(method, args);
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
