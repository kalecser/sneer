package sneer.bricks.softwaresharing.demolisher.impl;


import java.io.IOException;

import sneer.bricks.hardware.cpu.codecs.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.demolisher.Demolisher;
import sneer.foundation.lang.CacheMap;

class DemolisherImpl implements Demolisher {

	@Override
	public void demolishBuildingInto(CacheMap<String,BrickInfo> bricksByName, Sneer1024 srcFolderHash, boolean isCurrent) throws IOException {
		new Demolition(bricksByName, srcFolderHash, isCurrent);
	}

}
