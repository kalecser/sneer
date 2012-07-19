package sneer.bricks.hardware.io.prevalence.nature.impl;

import static basis.environments.Environments.my;
import static sneer.bricks.hardware.io.prevalence.nature.impl.PrevaylerHolder.building;
import basis.brickness.Brick;
import basis.brickness.Nature;
import basis.environments.Environment;
import basis.environments.NonBlockingEnvironment;
import basis.lang.Producer;
import sneer.bricks.hardware.io.prevalence.flag.PrevalenceFlag;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;

class PrevalenceEnvironment implements Environment {

	static final PrevalenceEnvironment INSTANCE = new PrevalenceEnvironment();
	static private final PrevalenceFlag FLAG = new PrevalenceFlag() { @Override public boolean isInsidePrevalence() {
		return true;
	}};

	
	private PrevalenceEnvironment() {}
	
	
	private final NonBlockingEnvironment delegate = (NonBlockingEnvironment)my(Environment.class); 

	
	@Override
	public <T> T provide(Class<T> brick) {
		if (PrevalenceFlag.class.isAssignableFrom(brick)) return (T)FLAG;
		
		if (!isPrevalent(brick))
			return delegate.provide(brick); //Could be a test environment.

		delegate.provideWithoutBlocking(brick); //This avoids deadlock in the case some other thread is already providing this brick and waiting for transaction log replay.
		return building().waitForInstance(brick);
	}

	
	<T> T provide(Class<T> prevalentBrick, Producer<T> instantiator) {
		T result = building().get(prevalentBrick, instantiator);
		if (!my(PrevalenceFlag.class).isInsidePrevalence())
			PrevaylerHolder.waitForTransactionLogReplayIfNecessary();
		return result;
	}
	
	
	private boolean isPrevalent(Class<?> brick) {
		Brick annotation = brick.getAnnotation(Brick.class);
		if (annotation == null) return false;
		
		for (Class<? extends Nature> nature : annotation.value())
			if (nature == Prevalent.class) return true;
			
		return false;
	}
	
}