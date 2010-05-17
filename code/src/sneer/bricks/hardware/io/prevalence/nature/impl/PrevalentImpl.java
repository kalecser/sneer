package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Arrays;
import java.util.List;

import sneer.bricks.hardware.io.prevalence.flag.PrevalenceFlag;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.lang.Producer;

class PrevalentImpl implements Prevalent {

	private final PrevalentBuilding _building = initBuilding();

	
	@Override
	public List<ClassDefinition> realize(Class<?> brick, ClassDefinition classDef) {
		return Arrays.asList(classDef);
	}

	
	@Override
	public synchronized <T> T instantiate(final Class<T> prevalentBrick, Class<T> implClassIgnored, final Producer<T> instantiator) {
		T existing = _building.provide(prevalentBrick);
		if (existing != null)
			return existing;

		BrickInstantiation<T> instantiation = new BrickInstantiation<T>(prevalentBrick, instantiator);

		return (T) (my(PrevalenceFlag.class).isInsidePrevalence()
			? instantiation.executeAndQuery(_building)
			: PrevaylerHolder._prevayler.execute(instantiation));
	}


	private PrevalentBuilding initBuilding() {
		return (PrevalentBuilding)PrevaylerHolder._prevayler.prevalentSystem(); 
	}
	
}