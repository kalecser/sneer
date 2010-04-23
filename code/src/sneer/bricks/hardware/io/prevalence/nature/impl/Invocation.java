package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Method;
import java.util.Date;

import org.prevayler.TransactionWithQuery;

import sneer.bricks.hardware.io.prevalence.map.PrevalentMap;
import sneer.bricks.hardware.io.prevalence.state.PrevailingState;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
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
	
	public Object executeAndQuery(final Object system, Date date) {
		Producer<Object> producer = new Producer<Object>() { @Override public Object produce() throws RuntimeException {
			final PrevalentBuilding building = (PrevalentBuilding)system;
			return EnvironmentUtils.produceIn(EnvironmentUtils.compose(building, my(Environment.class)), new Producer<Object>() { @Override public Object produce() throws RuntimeException {
				Object receiver = my(PrevalentMap.class).objectById(_id);
//				System.out.println("" + receiver + "[" + _id + "]." + _methodName + "()");
				return invoke(receiver, _methodName, _argTypes, Bubble.unmap(_args));
			}});
		}};
		return my(PrevailingState.class).produce(producer, producer);
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
