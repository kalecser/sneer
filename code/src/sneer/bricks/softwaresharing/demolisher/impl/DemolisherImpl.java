package sneer.bricks.softwaresharing.demolisher.impl;


import java.io.IOException;

import basis.lang.CacheMap;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.network.social.Contact;
import sneer.bricks.softwaresharing.BrickHistory;
import sneer.bricks.softwaresharing.demolisher.Demolisher;

class DemolisherImpl implements Demolisher {

	@Override
	public void demolishBuildingInto(CacheMap<String,BrickHistory> bricksByName, Hash srcFolderHash, Contact owner) throws IOException {
		new Demolition(bricksByName, srcFolderHash, owner);
	}

	@Override
	public CacheMap<String, BrickHistory> demolishOwnBuilding(Hash ownBuildingHash) throws IOException {
		CacheMap<String, BrickHistory> result = CacheMap.newInstance();
		demolishBuildingInto(result, ownBuildingHash, null);
		return result;
	}

}
