package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Date;

import org.prevayler.SureTransactionWithQuery;

import sneer.bricks.hardware.io.prevalence.map.PrevalenceMap;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.lang.Immutable;
import sneer.foundation.lang.Producer;

abstract class BuildingTransaction implements SureTransactionWithQuery, Producer<Object> {

	protected static final PrevalenceMap PrevalenceMap = my(PrevalenceMap.class);

	
	@Override
	public Object executeAndQuery(Object prevalentSystem, Date executionTime) {
		return executeAndQuery((PrevalentBuilding)prevalentSystem);
	}

	
	Object executeAndQuery(PrevalentBuilding building) {
		Environment environment = EnvironmentUtils.compose(building, my(Environment.class));
		return EnvironmentUtils.produceIn(environment, this);
	}

	
	@Override
	public Object produce() {
		Object result = execute();
		registerIfNecessary(result);
		return result;
	}

	
	protected abstract Object execute();

	
	private static void registerIfNecessary(Object result) {
		if (result == null) return;
		
		if (Immutable.isImmutable(result.getClass())) return;
		
		if (PrevalenceMap.isRegistered(result)) return;
		PrevalenceMap.register(result);
	}
}
