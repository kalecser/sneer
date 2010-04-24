package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Date;

import org.prevayler.SureTransactionWithQuery;

import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.lang.Producer;

abstract class BuildingTransaction<T> implements SureTransactionWithQuery, Producer<T> {

	@Override
	public Object executeAndQuery(Object prevalentSystem, Date executionTime) {
		return executeAndQuery((PrevalentBuilding)prevalentSystem);
	}

	private Object executeAndQuery(PrevalentBuilding building) {
		Environment environment = EnvironmentUtils.compose(building, my(Environment.class));
		return EnvironmentUtils.produceIn(environment, this);
	}

}