package sneer.bricks.softwaresharing.demolisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.FileVersion;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.Producer;

class BrickHistoryImpl implements BrickHistory {

	
	private final String _brickName;
	private final Register<Status> _status = my(Signals.class).newRegister(null);
	private final CacheMap<Hash, BrickVersionImpl> _versionsByHash = CacheMap.newInstance();
	private final Consumer<BrickVersion.Status> _statusRefresher = initStatusRefresher();
	private final List<Object> _refsToAvoidGc = new ArrayList<Object>();
	
	BrickHistoryImpl(String brickName) {
		my(Logger.class).log("BrickInfo created: " + brickName);
		_brickName = brickName;
	}


	private Consumer<BrickVersion.Status> initStatusRefresher() {
		return new Consumer<BrickVersion.Status>() { @Override public void consume(BrickVersion.Status ignored) {
			refreshStatus();
		}};
	}


	private void refreshStatus() {
		_status.setter().consume(calculateStatus());
	}


	private Status calculateStatus() {
		boolean hasCurrent = false, hasDifferent = false;
		
		if (versions().isEmpty()) return null; //Still being initialized.
		
		for (BrickVersion version : versions()) {
			if (version.status().currentValue() == BrickVersion.Status.DIFFERENT) hasDifferent = true;
			if (version.status().currentValue() == BrickVersion.Status.CURRENT  ) hasCurrent   = true;
		}

		if ( hasDifferent &&  hasCurrent) return Status.DIFFERENT;
		if ( hasDifferent && !hasCurrent) return Status.NEW;
		if (!hasDifferent &&  hasCurrent) return Status.CURRENT;

		throw new IllegalStateException();
	}


	@Override
	public boolean isSnapp() {
		return false; // Implement
	}

	
	@Override
	public String name() {
		return _brickName;
	}
	

	@Override
	public List<BrickVersion> versions() {
		ArrayList<BrickVersion> result = new ArrayList<BrickVersion>(_versionsByHash.values());
		Collections.sort(result, new Comparator<BrickVersion>() { @Override public int compare(BrickVersion a, BrickVersion b) {
			return a.status().currentValue().ordinal() - b.status().currentValue().ordinal();
		}});
		return result;
	}

	
	@Override
	public Signal<Status> status() {
		return _status.output();
	}


	@Override
	public void setChosenForExecution(BrickVersion version, boolean chosen) {
		if (!versions().contains(version))
			throw new IllegalArgumentException();
		
		((BrickVersionImpl)version).setChosenForExecution(chosen);
	}


	@Override
	public BrickVersion getVersionChosenForInstallation() {
		for (BrickVersion version : _versionsByHash.values())
			if (version.isChosenForExecution()) return version;
		
		return null;
	}

	void addVersionIfNecessary(Hash packageHash, Contact owner) throws IOException {
		Functor<String, byte[]> currentContentsFinder = new Functor<String, byte[]>() {  @Override public byte[] evaluate(String relativePath) {
			return currentContentsFor(relativePath);
		}};
		final BrickVersion current = currentVersion();
		final BrickVersionImpl newVersion = new BrickVersionImpl(packageHash, owner == null, currentContentsFinder, current);
		
		BrickVersionImpl versionKept = _versionsByHash.get(newVersion.hash(), new Producer<BrickVersionImpl>() { @Override public BrickVersionImpl produce() {
			my(Logger.class).log("Brick version found: " + newVersion.hash() + " version: " + (_versionsByHash.size() + 1));
			return newVersion;
		}});
		if (versionKept == newVersion) _refsToAvoidGc.add(newVersion.status().addReceiver(_statusRefresher));
		
		if (owner == null) versionKept.setCurrent(); //Previous kept version might not have been current.
		else versionKept.addUser(owner);
	}

	private BrickVersion currentVersion() {
		for (BrickVersion version : versions())
			if (version.status().currentValue() == BrickVersion.Status.CURRENT)
				return version;
		return null;
	}


	private byte[] currentContentsFor(String relativePath) {
		BrickVersion current = currentVersion();
		if (current != null)
			for (FileVersion file : current.files())
				if (file.status() == FileVersion.Status.CURRENT && file.relativePath().equals(relativePath))
					return file.contents();
		return null;
	}
}
