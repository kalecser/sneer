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
import sneer.foundation.lang.types.Classes;

class Bubble implements InvocationHandler {
	
	static CacheMap<Object, Object> _proxiesByObject = CacheMap.newInstance();
	
	static final List<Method> NO_PATH = Collections.emptyList();
	static final List<Object[]> NO_PATH_ARGS = Collections.emptyList();


	static <T> T wrap(T newObject) {
		T result = proxyFor(newObject);
		_proxiesByObject.put(newObject, result);
		return result;
	}


	private static <T> T proxyFor(T object) {
		return proxyFor(object, NO_PATH, NO_PATH_ARGS, object);
	}

	
	private static <T> T proxyFor(final Object startObject, final List<Method> queryPath, final List<Object[]> queryPathArgs, final T endObject) {
		InvocationHandler handler = new Bubble(startObject, queryPath, queryPathArgs);
		return (T)Proxy.newProxyInstance(endObject.getClass().getClassLoader(), Classes.allInterfacesOf(endObject.getClass()), handler);
	}

	
	private Bubble(Object startObject, List<Method> queryPath, List<Object[]> queryPathArgs) {
		_startObject = startObject;
		_queryPath = queryPath;
		_queryPathArgs = queryPathArgs;
	}


	private final Object _startObject;
	private final List<Method> _queryPath;
	private final List<Object[]> _queryPathArgs;
	
	
	@Override
	public Object invoke(Object proxyImplied, Method method, Object[] args) throws Throwable {
		return isTransaction(method)
			? handleTransaction(method, args)
			: handleQuery(method, args);
	}


	private boolean isTransaction(Method method) {
		if (method.getReturnType() == Void.TYPE) return true;
		if (method.getAnnotation(Transaction.class) != null) return true;
		return false;
	}
	
	
	private Object handleTransaction(Method method, Object[] args) {
		List<Method> extendedMethodPath = new ArrayList<Method>(_queryPath);
		List<Object[]> extendedMethodPathArgs = new ArrayList<Object[]>(_queryPathArgs);

		extendedMethodPath.add(method);
		extendedMethodPathArgs.add(args);

		InvocationChain invocation = new InvocationChain(_startObject, extendedMethodPath, extendedMethodPathArgs);
		Object result = PrevaylerHolder._prevayler.execute(invocation);
		
		return wrapIfNecessary(result, method, null, true);
	}

	
	private Object handleQuery(Method query, Object[] args) throws Throwable {
		Object result = invokeOnDelegate(query, args);
		return wrapIfNecessary(result, query, args, false);
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
		Object receiver = _startObject;
		for (Method getter : _queryPath)
			receiver = getter.invoke(receiver);
		return receiver;
	}

	
	private Object wrapIfNecessary(final Object returned, final Method method, final Object[] args, final boolean isTransaction) {
		if (returned == null) return null;

		Class<?> type = method.getReturnType();
		if (isReadOnly(type)) return returned;
		if (type.isArray()) return returned;
		
		return _proxiesByObject.get(returned, new Producer<Object>() { @Override public Object produce() {
			if (isRegistered(returned))
				return proxyFor(returned);
		
			if (isTransaction) throw new IllegalStateException("Transaction returned unregistered object: " + returned + ". Should have used " + ExportMap.class.getSimpleName() + " to register it before returning it.");
			
			List<Method> extendedQueryPath = new ArrayList<Method>(_queryPath);
			List<Object[]> extendedQueryPathArgs = new ArrayList<Object[]>(_queryPathArgs);
			
			extendedQueryPath.add(method);
			extendedQueryPathArgs.add(args);
			
			return proxyFor(_startObject, extendedQueryPath, extendedQueryPathArgs, returned);
		}});
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
