package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.prevayler.TransactionWithQuery;

class Invocation implements TransactionWithQuery {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

	
	Invocation(List<String> getterPath, Method method, Object[] args) {
		_getterPath = getterPath.toArray(EMPTY_STRING_ARRAY);
		_methodName = method.getName();
		_argTypes = method.getParameterTypes();
		_args = args;
	}

	
	private final String[] _getterPath;
	private final String _methodName;
	private final Class<?>[] _argTypes;
	private final Object[] _args;

	
	public Object executeAndQuery(Object brick, Date date) {
		Object receiver = navigateToReceiver(brick);
		return invoke(receiver, _methodName, _argTypes, _args);
	}

	
	private Object navigateToReceiver(Object brick) {
		Object result = brick;
		for (int i = 0; i < _getterPath.length; i++)
			result = invoke(result, _getterPath[i], EMPTY_CLASS_ARRAY);
		return result;
	}


	private Object invoke(Object receiver, String methodName, Class<?>[] argTypes, Object... args) {
		try {
			Method method = receiver.getClass().getMethod(methodName, argTypes);
			method.setAccessible(true);
			return method.invoke(receiver, args);
		} catch (Exception e) {
			throw new IllegalStateException("Exception trying to invoke " + receiver.getClass() + "." + methodName, e);
		}
	}


	private static final long serialVersionUID = 1L;

}
