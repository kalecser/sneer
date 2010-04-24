package sneer.foundation.environments;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;


public class ProxyInEnvironment implements InvocationHandler {

	
	private static final Class<?>[] CLASS_ARRAY_TYPE = new Class[]{};

	
	public static <T> T newInstance(T component) {
		return newInstance(my(Environment.class), component);
	}

	
	public static <T> T newInstance(Environment environment, final T component) {
		final Class<? extends Object> componentClass = component.getClass();
		final ProxyInEnvironment invocationHandler = new ProxyInEnvironment(environment, component);
		Class<?>[] interfaces = interfacesImplementedBy(componentClass);
		return (T)Proxy.newProxyInstance(componentClass.getClassLoader(), interfaces, invocationHandler);
	}
	
	
	private static Class<?>[] interfacesImplementedBy(Class<?> clazz) {
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		while (clazz != Object.class) {
			for (Class<?> intrface :clazz.getInterfaces())
				result.add(intrface);
			clazz = clazz.getSuperclass();
		}
		return result.toArray(CLASS_ARRAY_TYPE);
	}


	private final Environment _environment;
	private final Object _delegate;

	
	private ProxyInEnvironment(Environment environment, Object component) {
		_environment = environment;
		_delegate = component;
	}

	
	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		final ByRef<Object> result = ByRef.newInstance();

		Environments.runWith(_environment, new Closure() { @Override public void run() {
			try {
				result.value = method.invoke(_delegate, args);
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException();
			} catch (IllegalAccessException e) {
				throw new IllegalStateException();
			} catch (InvocationTargetException e) {
				result.value = e.getCause();
			}
		}});
		
		if (result.value == null)
			return null;
		
		if (result.value instanceof Throwable)
			throw (Throwable)result.value;
		
		return result.value;
	}

}