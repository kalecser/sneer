package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Method;
import java.util.Date;

import org.prevayler.TransactionWithQuery;

import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;
class Invocation implements TransactionWithQuery {
		
	Invocation(long id, Method method, Object[] args) {
		_id = id;
		_methodName = method.getName();
		_argTypes = method.getParameterTypes();	
		_args = args;	
	}


	private final long _id;
	private final String _methodName;
	private final Class<?>[] _argTypes;
	private final Object[] _args;
	
	private final Producer<Object> SHOULD_NOT_BE_PREVAILING = new Producer<Object>() { @Override public Object produce() throws RuntimeException {
		throw new IllegalStateException();
	}};
	
	public Object executeAndQuery(final Object system, Date date) {
		
		return InPrevailingState.produce(SHOULD_NOT_BE_PREVAILING, new Producer<Object>() { @Override public Object produce() throws RuntimeException {
			final PrevalentBuilding building = (PrevalentBuilding)system;
			
			final ByRef<Object> retVal = ByRef.newInstance();
			Environments.runWith(EnvironmentUtils.compose(building, my(Environment.class)), new Closure() { @Override public void run() {
				Object receiver = building.objectById(_id);
				retVal.value = invoke(receiver, _methodName, _argTypes, _args);
			}});
			return retVal.value;
		}});
		
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
