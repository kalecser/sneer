package sneer.bricks.softwaresharing.demolisher;


import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.CacheMap;

@Brick
/** "Demolishes" a brick building contained in a source folder and retrieves individual brick info.*/
public interface Demolisher {

	void demolishBuilding(CacheMap<String,BrickInfo> bricksByName, Sneer1024 srcFolderHash, boolean isCurrent);

}
