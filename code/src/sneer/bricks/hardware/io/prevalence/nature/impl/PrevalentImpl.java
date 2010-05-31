package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.util.Arrays;
import java.util.List;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.lang.Producer;

class PrevalentImpl implements Prevalent {

	@Override
	public List<ClassDefinition> realize(Class<?> brick, ClassDefinition classDef) {
		return Arrays.asList(classDef);
	}

	
	@Override
	public <T> T instantiate(final Class<T> prevalentBrick, Class<T> implClassIgnored, final Producer<T> instantiator) {
		T brickInstance = PrevalenceEnvironment.INSTANCE.provide(prevalentBrick, instantiator);
		
		return (T)Bubble.wrapped(brickInstance, new BrickProvision(prevalentBrick, brickInstance));
	}

}