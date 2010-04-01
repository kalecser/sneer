package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
<<<<<<< Updated upstream:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java
import java.util.ArrayList;
=======
>>>>>>> Stashed changes:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java
import java.util.Arrays;
import java.util.List;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

class PrevalentImpl implements Prevalent {
	
<<<<<<< Updated upstream:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java
	private final List<Prevayler> _prevaylers = new ArrayList<Prevayler>();
=======
	private Prevayler _prevayler;
>>>>>>> Stashed changes:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java

	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;


	{
		_refToAvoidGc = my(Threads.class).crashing().addPulseReceiver(new Closure() { @Override public void run() {
			crash();
		}});
	}
	
	
	@Override
	public List<ClassDefinition> realize(ClassDefinition classDef) {
		return Arrays.asList(classDef);
	}

<<<<<<< Updated upstream:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java

	@Override
	public <T> T instantiate(Class<T> brick, Class<T> implClass, Producer<T> producer) {
		Prevayler prevayler = createPrevayler(producer.produce(), prevalenceBase(brick));
		_prevaylers.add(prevayler);
		return (T)Bubble.wrapStateMachine(prevayler);
	}


	private <T> File prevalenceBase(Class<T> brick) {
		return my(FolderConfig.class).storageFolderFor(brick);
	}


	private Prevayler createPrevayler(Object system, File prevalenceBase) {
		PrevaylerFactory factory = createPrevaylerFactory(system, prevalenceBase);
=======
	boolean _prevailing;
	
	@Override
	public synchronized <T> T instantiate(final Class<T> brick, Class<T> implClass, final Producer<T> producer) {
		
		if (_prevailing)
			return Bubble.wrap(_prevayler, brick, producer.produce());
		
		_prevailing = true;
		try {
			if (null == _prevayler)
				_prevayler = createPrevayler(prevalenceBase());
			
			PrevalentBuilding building = (PrevalentBuilding) _prevayler.prevalentSystem();
			T existing = building.brick(brick);
			T instance = existing != null
				? existing
				: (T)_prevayler.execute(new InstantiateBrick<T>(brick, producer));
			
			return Bubble.wrap(_prevayler, brick, instance);
		} finally {
			_prevailing = false;
		}
	}

	private <T> File prevalenceBase() {
		return my(FolderConfig.class).storageFolderFor(Prevalent.class);
	}

	private Prevayler createPrevayler(File prevalenceBase) {
		PrevaylerFactory factory = createPrevaylerFactory(new PrevalentBuilding(), prevalenceBase);
>>>>>>> Stashed changes:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java

		try {
			return factory.create();
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} catch (ClassNotFoundException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

<<<<<<< Updated upstream:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java

=======
>>>>>>> Stashed changes:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java
	private PrevaylerFactory createPrevaylerFactory(Object system, File prevalenceBase) {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(system);
		factory.configurePrevalenceDirectory(prevalenceBase.getAbsolutePath());
		factory.configureTransactionFiltering(false);
<<<<<<< Updated upstream:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java
		factory.configureJournalSerializer("xstreamjournal", new SerializerWithClassLoader(PrevalentImpl.class.getClassLoader()));
		return factory;
	}

	
	private void crash() {
		for (Prevayler prevayler : _prevaylers) crash(prevayler);
		_prevaylers.clear();
	}


	private void crash(Prevayler prevayler) {
		try {
			prevayler.close();
=======
		factory.configureJournalSerializer("xstreamjournal", new SerializerWithClassMapper());
		return factory;
	}
	
	private void crash() {
		try {
			_prevayler.close();
>>>>>>> Stashed changes:code/src/sneer/bricks/hardware/io/prevalence/nature/impl/PrevalentImpl.java
		} catch (IOException e) {
			my(Logger.class).log("Exception closing prevayler: " + e);
		}
	}
}