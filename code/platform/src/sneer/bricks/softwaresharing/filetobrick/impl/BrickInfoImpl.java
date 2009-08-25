package sneer.bricks.softwaresharing.filetobrick.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.exceptions.NotImplementedYet;

class BrickInfoImpl implements BrickInfo {

	
	private final String _brickName;
	private final CacheMap<Sneer1024, BrickVersionImpl> _versionsByHash = new CacheMap<Sneer1024, BrickVersionImpl>();

	
	public BrickInfoImpl(String brickName) {
		my(Logger.class).log("BrickInfo created: " + brickName);
		_brickName = brickName;
	}


	@Override
	public boolean isSnapp() {
		throw new NotImplementedYet(); // Implement
	}

	
	@Override
	public String name() {
		return _brickName;
	}
	

	@Override
	public List<BrickVersion> versions() {
		return new ArrayList<BrickVersion>(_versionsByHash.values());
	}

	
	@Override
	public Status status() {
		boolean hasCurrent = false, hasDifferent = false;
		for (BrickVersion version : versions()) {
			if (version.status().equals(BrickVersion.Status.DIFFERENT)) hasDifferent = true;
			if (version.status().equals(BrickVersion.Status.CURRENT)) hasCurrent = true;
		}

		if ( hasDifferent &&  hasCurrent) return Status.DIFFERENT;
		if ( hasDifferent && !hasCurrent) return Status.NEW;
		if (!hasDifferent &&  hasCurrent) return Status.CURRENT;
		throw new IllegalStateException();
	}


	@Override
	public void setStagedForInstallation(BrickVersion version, boolean staged) {
		if (!versions().contains(version))
			throw new IllegalArgumentException();
		
		((BrickVersionImpl)version).setStagedForExecution(staged);
	}


	@Override
	public BrickVersion getVersionStagedForInstallation() {
		for (BrickVersion version : _versionsByHash.values())
			if (version.isStagedForExecution()) return version;
		
		return null;
	}


	void addVersion(final Sneer1024 versionHash, final boolean isCurrent) {
		BrickVersionImpl version = _versionsByHash.get(versionHash, new Producer<BrickVersionImpl>() { @Override public BrickVersionImpl produce() {
			return new BrickVersionImpl(versionHash, isCurrent);
		}});
		
		if (isCurrent) version.setCurrent();
	}

}
