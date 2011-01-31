package sneer.bricks.softwaresharing.publisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.publisher.BuildingHash;
import sneer.bricks.softwaresharing.publisher.BuildingPublisher;

class BuildingPublisherImpl implements BuildingPublisher {

	@Override
	public BuildingHash publishMyOwnBuilding() throws IOException {
		Hash hash;
		try {
			hash = my(FileMapper.class).mapFileOrFolder(srcFolder());
		} catch (MappingStopped e) {
			throw new IllegalStateException(e);
		}

		BuildingHash result = new BuildingHash(hash);
		my(TupleSpace.class).add(result);
		return result;
	}

	private File srcFolder() {
		return my(FolderConfig.class).srcFolder().get();
	}

}
