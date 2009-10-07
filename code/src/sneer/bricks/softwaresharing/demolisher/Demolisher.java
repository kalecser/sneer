package sneer.bricks.softwaresharing.demolisher;


import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.CacheMap;

@Brick
public interface Demolisher {

	/** "Demolishes" a brick building contained in a source folder and stores individual brick info into bricksByName.*/
	void demolishBuildingInto(CacheMap<String,BrickInfo> bricksByName, Sneer1024 srcFolderHash, boolean isMyOwn);

}
