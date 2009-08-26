package sneer.bricks.softwaresharing.demolisher.impl;


import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.demolisher.Demolisher;
import sneer.foundation.lang.CacheMap;

class DemolisherImpl implements Demolisher {

	@Override
	public void demolishBuilding(CacheMap<String,BrickInfo> bricksByName, Sneer1024 srcFolderHash, boolean isCurrent) {
		new FileToBrickConversion(bricksByName, srcFolderHash, isCurrent);
	}

}
