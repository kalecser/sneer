package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.lang.Closure;

class PrevaylerHolder {

	static final Prevayler _prevayler = createPrevayler(prevalenceBase());

	@SuppressWarnings("unused")	static private final WeakContract _refToAvoidGc;


	static {
		_refToAvoidGc = my(Threads.class).crashed().addPulseReceiver(new Closure() { @Override public void run() {
			crash();
		}});
	}
	

	private static <T> File prevalenceBase() {
		return my(FolderConfig.class).storageFolderFor(Prevalent.class);
	}

	
	private static Prevayler createPrevayler(final File prevalenceBase) {
		final PrevaylerFactory factory = createPrevaylerFactory(new PrevalentBuilding(), prevalenceBase);

		try {
			return factory.create();
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} catch (ClassNotFoundException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

		
	private static PrevaylerFactory createPrevaylerFactory(Object system, File prevalenceBase) {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(system);
		factory.configurePrevalenceDirectory(prevalenceBase.getAbsolutePath());
		factory.configureTransactionFiltering(false);
		factory.configureJournalSerializer("xstreamjournal", new SerializerAdapter());
		return factory;
	}
	
	
	private static void crash() {
		try {
			_prevayler.close();
		} catch (IOException e) {
			my(Logger.class).log("Exception closing prevayler: " + e);
		}
	}
}