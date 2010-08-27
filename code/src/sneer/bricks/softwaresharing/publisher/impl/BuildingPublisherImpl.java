package sneer.bricks.softwaresharing.publisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.publisher.BuildingPublisher;
import sneer.bricks.softwaresharing.publisher.BuildingHash;

class BuildingPublisherImpl implements BuildingPublisher {

	@Override
	public void publishMyOwnBuilding() {
		GitWorkaround.standardizeLastModifiedDatesWhileWeStillUseGitBecauseGitDoesNotPreserveThem(srcFolder());

		Hash hash;
		try {
			hash = my(FileMapper.class).mapFileOrFolder(srcFolder());
		} catch (MappingStopped e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error while reading bricks' code.", "", e);
			return;
		}

		my(TupleSpace.class).acquire(new BuildingHash(hash));
	}

	private File srcFolder() {
		return my(FolderConfig.class).srcFolder().get();
	}

}
