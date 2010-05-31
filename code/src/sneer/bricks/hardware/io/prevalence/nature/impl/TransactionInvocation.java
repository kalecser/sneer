package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Method;
import java.util.Date;

import org.prevayler.SureTransactionWithQuery;

import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.lang.Producer;

public class TransactionInvocation extends Invocation implements SureTransactionWithQuery {

	TransactionInvocation(Producer<Object> targetProducer, Method method, Object[] args) {
		super(targetProducer, method, args);
	}
	
	
	@Override
	public Object executeAndQuery(Object prevalentSystem, Date executionTimeIgnored) {
		PrevaylerHolder.setBuildingIfNecessary((PrevalentBuilding)prevalentSystem);
		return EnvironmentUtils.produceIn(PrevalenceEnvironment.INSTANCE, this);
	}

	
	@Override
	public Object produce() {
		try {
			return produceAndRegister();
		} catch (RuntimeException rx) {
			my(ExceptionLogger.class).log(rx);
			throw rx;
		}
	}


	private Object produceAndRegister() {
		Object result = super.produce();
		registerIfNecessary(result);
		return result;
	}


	private static void registerIfNecessary(Object object) {
		if (!PrevalenceMap.requiresRegistration(object)) return;
		if (PrevalenceMap.isRegistered(object)) return;
		PrevalenceMap.register(object);
	}

	
}
