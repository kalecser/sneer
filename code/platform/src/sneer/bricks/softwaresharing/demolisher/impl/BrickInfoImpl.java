package sneer.bricks.softwaresharing.demolisher.impl;

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
			if (version.status().equals(BrickVersion.Status.CURRENT  )) hasCurrent   = true;
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


	void addVersionFromPackage(Sneer1024 packageHash, boolean isCurrent) {
		
		final BrickVersionImpl newVersion = new BrickVersionImpl(packageHash, isCurrent);
		BrickVersionImpl versionKept = _versionsByHash.get(newVersion.hash(), new Producer<BrickVersionImpl>() { @Override public BrickVersionImpl produce() {
			my(Logger.class).log("Brick version found: " + newVersion.hash());
			return newVersion;
		}});
		
		if (isCurrent) versionKept.setCurrent(); //Previous kept version might not have been current.
	}

}
