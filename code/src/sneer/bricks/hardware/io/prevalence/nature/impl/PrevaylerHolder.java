package sneer.bricks.hardware.io.prevalence.nature.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import basis.lang.Closure;
import basis.util.concurrent.Latch;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.software.folderconfig.FolderConfig;

class PrevaylerHolder {

	static Prevayler _prevayler;
	static private final Latch _transactionLogReplayed = new Latch();

	static private PrevalentBuilding _building;
	static private final Latch _buildingAvailable = new Latch();


	
	@SuppressWarnings("unused")	static private final WeakContract _refToAvoidGc;


	static {
		startReplayingTransactions();
		
		_refToAvoidGc = my(Threads.class).crashed().addPulseReceiver(new Closure() { @Override public void run() {
			crash();
		}});
	}
	

	private static void startReplayingTransactions() {
		my(Threads.class).startDaemon("Prevalent transaction log replay.", new Closure() { @Override public void run() {
			_prevayler = createPrevayler(prevalenceBase());
			setBuildingIfNecessary((PrevalentBuilding)_prevayler.prevalentSystem());
			_transactionLogReplayed.open();
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
			throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} catch (ClassNotFoundException e) {
			throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
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


	static void setBuildingIfNecessary(PrevalentBuilding building) {
		if (_building == null) {
			_building = building;
			_buildingAvailable.open();
		}
		if (building != _building) throw new IllegalStateException();
	}


	static PrevalentBuilding building() {
		_buildingAvailable.waitTillOpen();
		return _building;
	}


	static boolean isReplayingTransactions() {
		return !_transactionLogReplayed.isOpen();
	}


	static void waitForTransactionLogReplayIfNecessary() {
		_transactionLogReplayed.waitTillOpen();
	}

}