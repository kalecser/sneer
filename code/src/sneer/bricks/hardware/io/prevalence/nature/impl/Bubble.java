package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.io.prevalence.map.ExportMap;
import sneer.bricks.hardware.io.prevalence.nature.Transaction;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.ReadOnly;

class Bubble implements InvocationHandler {
	
	static CacheMap<Object, Object> _proxiesByDelegate = CacheMap.newInstance();
	
	static final List<Method> NO_PATH = Collections.emptyList();
	

	static <T> T wrap(final T object) {
		return wrap(object, NO_PATH, object);
	}

	
	static <T> T wrap(final Object startObject, final List<Method> methodPath, final T endObject) {
		return (T) _proxiesByDelegate.get(endObject, new Producer<Object>() { @Override public Object produce() {
			InvocationHandler handler = new Bubble(startObject, methodPath);
			return Proxy.newProxyInstance(endObject.getClass().getClassLoader(), endObject.getClass().getInterfaces(), handler);
		}});
	}
	
	
	private Bubble(Object delegate, List<Method> method) {
		_delegate = delegate;
		_getterPath = method;
		if (_getterPath == null)
			Invocation.preApprove(_delegate);
	}

	
	private final Object _delegate;
	private final List<Method> _getterPath;
	
	
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
		return PrevaylerHolder._prevayler.execute(new Invocation(_delegate, method, args, getterPath()));
	}
	
	
	private List<String> getterPath() {
		ArrayList<String> getterPath = new ArrayList<String>(_getterPath.size());
		for (Method m : _getterPath) getterPath.add(m.getName());
		return getterPath;
	}

	
	private Object handleQuery(Method method, Object[] args) throws Throwable {
		return invokeOnDelegate(method, args);
	}
	
	
	private Object invokeOnDelegate(Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(navigateToReceiver(), args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}


	private Object navigateToReceiver() throws IllegalAccessException, InvocationTargetException {
		Object receiver = _delegate;
		for (Method getter : _getterPath)
			receiver = getter.invoke(receiver);
		return receiver;
	}

	
	private Object wrapIfNecessary(Object result, Method method) {
		if (result == null) return null;

		Class<?> type = method.getReturnType();
		if (isReadOnly(type)) return result;
		if (type.isArray()) return result;
		
		if (isRegistered(result))
			return wrap(result);
		
		List<Method> newGetterPath = new ArrayList<Method>(_getterPath);
		newGetterPath.add(method);
		return wrap(_delegate, newGetterPath, result);
	}

	
	private boolean isRegistered(Object object) {
		return my(ExportMap.class).isRegistered(object);
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
