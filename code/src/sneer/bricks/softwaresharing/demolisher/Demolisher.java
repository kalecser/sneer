package sneer.bricks.softwaresharing.demolisher;


import java.io.IOException;

import basis.brickness.Brick;
import basis.lang.CacheMap;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.network.social.Contact;
import sneer.bricks.softwaresharing.BrickHistory;

@Brick
public interface Demolisher {

	/** "Demolishes" own brick building contained in a source folder and stores individual brick info into bricksByName.
	 * @throws IOException */	
	CacheMap<String, BrickHistory> demolishOwnBuilding(Hash ownBuildingHash) throws IOException;
	
	/** "Demolishes" a brick building contained in a source folder and stores individual brick info into bricksByName.
	 * @throws IOException */
	void demolishBuildingInto(CacheMap<String,BrickHistory> bricksByName, Hash srcFolderHash, Contact owner) throws IOException;

}
