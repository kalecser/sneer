package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

import sneer.bricks.hardware.io.prevalence.flag.PrevalenceFlag;
import sneer.bricks.hardware.io.prevalence.map.PrevalenceMap;
import sneer.bricks.hardware.io.prevalence.nature.Transaction;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Immutable;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.ReadOnly;
import sneer.foundation.lang.types.Classes;

class Bubble implements InvocationHandler {
	
	private static final PrevalenceMap PrevalenceMap = my(PrevalenceMap.class);
	
	private static final CacheMap<Object, Object> _proxiesByObject = CacheMap.newInstance();
	

	static <T> T wrapped(final Object object, final Producer<Object> path) {
		return (T)_proxiesByObject.get(object, new Producer<Object>() { @Override public Object produce() {
			return newProxyFor(object, path);
		}});
	}


	private static Object newProxyFor(Object object, Producer<Object> path) {
		if (isRegistered(object))
			path = new MapLookup(object);

		InvocationHandler handler = new Bubble(path);
		Class<?> delegateClass = object.getClass();
		return Proxy.newProxyInstance(delegateClass.getClassLoader(), Classes.allInterfacesOf(delegateClass), handler);
	}


	private Bubble(Producer<Object> producer) {
		_invocationPath = producer;
	}


	private final Producer<Object> _invocationPath;
	
	
	@Override
	public Object invoke(Object proxyImplied, Method method, Object[] args) throws Throwable {
		Producer<Object> path = extendedPath(method, args);
		Object result = path.produce();
		return wrapIfNecessary(result, path);
	}


	private Object wrapIfNecessary(Object object, Producer<Object> path) {
		if (object == null) return object;
		
		Class<?> type = object.getClass();
		if (type.isArray()) return object;
		if (Collection.class.isAssignableFrom(type)) return object;
		if (Immutable.isImmutable(type)) return object;
		
		if (ReadOnly.class.isAssignableFrom(type)) return object;
		
		return wrapped(object, path);
	}


	private Producer<Object> extendedPath(Method method, Object[] args) {
		if (!isTransaction(method))
			return new Invocation(_invocationPath, method, args);

		TransactionInvocation transaction = new TransactionInvocation(_invocationPath, method, args);
		return my(PrevalenceFlag.class).isInsidePrevalence()
			? transaction
			: withPrevayler(transaction);
	}


	private Producer<Object> withPrevayler(final TransactionInvocation transaction) {
		return new Producer<Object>() { @Override public Object produce() {
			return PrevaylerHolder._prevayler.execute(transaction);
		}};
	}


	private boolean isTransaction(Method method) {
		if (method.getReturnType() == Void.TYPE) return true;
		if (method.getAnnotation(Transaction.class) != null) return true;
		return false;
	}
	
	
	private static boolean isRegistered(Object object) {
		return PrevalenceMap.isRegistered(object);
	}

}
