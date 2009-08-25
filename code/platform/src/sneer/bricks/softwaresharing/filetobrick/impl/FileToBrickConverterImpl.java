package sneer.bricks.softwaresharing.filetobrick.impl;


import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.filetobrick.FileToBrickConverter;
import sneer.foundation.lang.CacheMap;

class FileToBrickConverterImpl implements FileToBrickConverter {

	@Override
	public void accumulateBricksFromCachedSrcFolder(CacheMap<String,BrickInfo> bricksByName, Sneer1024 srcFolderHash) {
		new FileToBrickConversion(bricksByName, srcFolderHash);
	}

}
