package sneer.bricks.softwaresharing.demolisher;


import java.io.IOException;

import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.softwaresharing.BrickInfo;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.CacheMap;

@Brick
public interface Demolisher {

	/** "Demolishes" a brick building contained in a source folder and stores individual brick info into bricksByName.
	 * @throws IOException */
	void demolishBuildingInto(CacheMap<String,BrickInfo> bricksByName, Sneer1024 srcFolderHash, boolean isMyOwn) throws IOException;

}
