package sneer.bricks.softwaresharing.publisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.codecs.crypto.Sneer1024;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.publisher.SourcePublisher;
import sneer.bricks.softwaresharing.publisher.SrcFolderHash;

class SourcePublisherImpl implements SourcePublisher {

	@Override
	public void publishSourceFolder() {
		GitWorkaround.standardizeLastModifiedDatesWhileWeStillUseGitBecauseGitDoesNotPreserveThem(srcFolder());

		Sneer1024 hash;
		try {
			hash = my(FileMapper.class).mapFolder(srcFolder());
		} catch (MappingStopped e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error while reading bricks' code.", "", e);
			return;
		}

		my(TupleSpace.class).acquire(new SrcFolderHash(hash));
	}

	private File srcFolder() {
		return my(FolderConfig.class).srcFolder().get();
	}

}
