/**
 * 
 */
package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Date;

import org.prevayler.SureTransactionWithQuery;

import sneer.bricks.hardware.io.prevalence.map.PrevalentMap;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

final class InstantiateBrick<T> implements
		SureTransactionWithQuery {
	
	private final Class<T> _brick;
	private final Producer<T> _producer;

	InstantiateBrick(Class<T> brick, Producer<T> producer) {
		_brick = brick;
		_producer = producer;
	}

	@Override
	public Object executeAndQuery(Object prevalentSystem, Date executionTime) {
		
		final PrevalentBuilding building = (PrevalentBuilding)prevalentSystem;
		final ByRef<T> retVal = ByRef.newInstance();
		Environments.runWith(EnvironmentUtils.compose(building, my(Environment.class)), new Closure() { @Override public void run() {
			retVal.value = my(PrevalentMap.class).register(_producer.produce());
		}});
		building.add(_brick, retVal.value);
		return retVal.value;
	}
}