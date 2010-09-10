package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Method;
import java.util.Date;

import org.prevayler.TransactionWithQuery;

import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.lang.ProducerX;

public class TransactionInvocation extends Invocation implements TransactionWithQuery {

	TransactionInvocation(ProducerX<Object, ? extends Exception> targetProducer, Method method, Object[] args) {
		super(targetProducer, method, args);
	}
	
	
	@Override
	public Object executeAndQuery(Object prevalentSystem, Date executionTimeIgnored) throws Exception {
		PrevaylerHolder.setBuildingIfNecessary((PrevalentBuilding)prevalentSystem);
		return EnvironmentUtils.produceIn(PrevalenceEnvironment.INSTANCE, this);
	}

	
	@Override
	public Object produce() throws Exception {
		try {
			return produceAndRegister();
		} catch (RuntimeException rx) {
			if (PrevaylerHolder.isReplayingTransactions())
				my(ExceptionLogger.class).log(rx, "Exception thrown while replaying prevalent transactions: ", rx.getMessage());
			throw rx;
		}
	}


	private Object produceAndRegister() throws Exception {
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
