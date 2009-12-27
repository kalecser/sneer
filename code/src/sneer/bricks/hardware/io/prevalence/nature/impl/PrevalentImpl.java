package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.lang.Producer;

class PrevalentImpl implements Prevalent {
	
	@Override
	public List<ClassDefinition> realize(ClassDefinition classDef) {
		return Arrays.asList(classDef);
	}


	@Override
	public <T> T instantiate(Class<T> brick, Class<T> implClass,	Producer<T> producer) {
		Prevayler prevayler = createPrevayler(producer.produce(), prevalenceBase(brick));
		return (T)Bubble.wrapStateMachine(prevayler);
	}


	private <T> File prevalenceBase(Class<T> brick) {
		return my(FolderConfig.class).storageFolderFor(brick);
	}


	private Prevayler createPrevayler(Object system, File prevalenceBase) {
		PrevaylerFactory factory = createPrevaylerFactory(system, prevalenceBase);

		try {
			return factory.create();
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} catch (ClassNotFoundException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}


	private PrevaylerFactory createPrevaylerFactory(Object system, File prevalenceBase) {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(system);
		factory.configurePrevalenceDirectory(prevalenceBase.getAbsolutePath());
		factory.configureTransactionFiltering(false);
		factory.configureJournalSerializer("xstreamjournal", new SerializerWithClassLoader(PrevalentImpl.class.getClassLoader()));
		return factory;
	}
	
}