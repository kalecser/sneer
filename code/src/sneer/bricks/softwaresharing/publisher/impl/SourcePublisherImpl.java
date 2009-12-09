package sneer.bricks.softwaresharing.publisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.publisher.SourcePublisher;
import sneer.bricks.softwaresharing.publisher.SrcFolderHash;

class SourcePublisherImpl implements SourcePublisher {
	
	private final Light _errorLight = my(BlinkingLights.class).prepare(LightType.ERROR);

	
	@Override
	public void publishSourceFolder() {
		GitWorkaround.standardizeLastModifiedDatesWhileWeStillUseGitBecauseGitDoesNotPreserveThem(srcFolder());
		
		Sneer1024 hash;
		try {
			hash = my(FileMap.class).put(srcFolder());
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(_errorLight, "Error reading your source folder.", "There was trouble trying to read your source folder in order to publish your bricks for your peers. See log for details.", e);
			return;
		}
		
		my(TupleSpace.class).publish(new SrcFolderHash(hash));
	}


	private File srcFolder() {
		return my(FolderConfig.class).srcFolder().get();
	}
	
}
