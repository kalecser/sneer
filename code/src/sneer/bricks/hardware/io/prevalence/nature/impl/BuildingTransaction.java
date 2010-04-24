package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.util.Date;

import org.prevayler.SureTransactionWithQuery;

abstract class BuildingTransaction implements SureTransactionWithQuery {

	@Override
	public Object executeAndQuery(Object prevalentSystem, Date executionTime) {
		return executeAndQuery((Building)prevalentSystem);
	}

	protected abstract Object executeAndQuery(Building building);


}