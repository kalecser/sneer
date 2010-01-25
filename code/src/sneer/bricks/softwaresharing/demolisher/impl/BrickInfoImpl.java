package sneer.bricks.softwaresharing.demolisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.cpu.codecs.crypto.Sneer1024;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class BrickInfoImpl implements BrickInfo {

	
	private final String _brickName;
	private final CacheMap<Sneer1024, BrickVersionImpl> _versionsByHash = CacheMap.newInstance();

	
	public BrickInfoImpl(String brickName, Sneer1024 packageHash, boolean isCurrent) throws IOException {
		my(Logger.class).log("BrickInfo created: " + brickName);
		_brickName = brickName;
		addVersionIfNecessary(packageHash, isCurrent);
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


	void addVersionIfNecessary(Sneer1024 packageHash, boolean isCurrent) throws IOException {
		final BrickVersionImpl newVersion = new BrickVersionImpl(packageHash, isCurrent);
		BrickVersionImpl versionKept = _versionsByHash.get(newVersion.hash(), new Producer<BrickVersionImpl>() { @Override public BrickVersionImpl produce() {
			my(Logger.class).log("Brick version found: " + newVersion.hash() + " version: " + (_versionsByHash.size() + 1));
			return newVersion;
		}});
		
		if (isCurrent) versionKept.setCurrent(); //Previous kept version might not have been current.
	}

}
