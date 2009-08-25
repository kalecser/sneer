package sneer.bricks.softwaresharing.filetobrick;


import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.CacheMap;

@Brick
public interface FileToBrickConverter {

	void accumulateBricksFromCachedSrcFolder(CacheMap<String,BrickInfo> bricksByName, Sneer1024 srcFolderHash);

}
