/**
 * 
 */
package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Date;

import org.prevayler.SureTransactionWithQuery;

import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.lang.Producer;

final class InstantiateBrick<T> implements SureTransactionWithQuery {
	
	private final Class<T> _brick;
	private final Producer<T> _producer;

	
	InstantiateBrick(Class<T> brick, Producer<T> producer) {
		_brick = brick;
		_producer = producer;
	}

	
	@Override
	public Object executeAndQuery(Object prevalentSystem, Date executionTime) {
		final PrevalentBuilding building = (PrevalentBuilding)prevalentSystem;
		
		Environment environment = EnvironmentUtils.compose(building, my(Environment.class));
		T result = EnvironmentUtils.produceIn(environment, _producer);
		building.add(_brick, result);
		return result;
	}
}