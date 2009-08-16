package sneer.bricks.softwaresharing.filetobrick.impl;

import java.util.Collection;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.bricks.softwaresharing.filetobrick.FileToBrickConverter;

class FileToBrickConverterImpl implements FileToBrickConverter {

	@Override
	public Collection<BrickInfo> bricksInCachedSrcFolders(Collection<Sneer1024> srcFolderHashes) {
		return new FileToBrickConversion(srcFolderHashes)._result;
	}

}
