package sneer.bricks.softwaresharing.demolisher;


import java.io.IOException;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.network.social.Contact;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.CacheMap;

@Brick
public interface Demolisher {

	/** "Demolishes" own brick building contained in a source folder and stores individual brick info into bricksByName.
	 * @throws IOException */	
	CacheMap<String, BrickHistory> demolishOwnBuilding(Hash ownBuildingHash) throws IOException;
	
	/** "Demolishes" a brick building contained in a source folder and stores individual brick info into bricksByName.
	 * @throws IOException */
	void demolishBuildingInto(CacheMap<String,BrickHistory> bricksByName, Hash srcFolderHash, Contact owner) throws IOException;

}
