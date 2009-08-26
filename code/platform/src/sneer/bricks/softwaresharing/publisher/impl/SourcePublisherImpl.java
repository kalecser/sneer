package sneer.bricks.softwaresharing.publisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardwaresharing.files.publisher.FilePublisher;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.publisher.SourcePublisher;
import sneer.bricks.softwaresharing.publisher.SrcFolderHash;

class SourcePublisherImpl implements SourcePublisher {
	
	private final Light _errorLight = my(BlinkingLights.class).prepare(LightType.ERROR);

	
	@Override
	public void publishSourceFolder() {
		Sneer1024 hash;
		try {
			hash = my(FilePublisher.class).publish(platformSrcFolder());
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(_errorLight, "Error publishing bricks.", helpMessage(), e);
			return;
		}
		
		my(TupleSpace.class).publish(new SrcFolderHash(hash));
	}


	private File platformSrcFolder() {
		return my(FolderConfig.class).platformSrcFolder().get();
	}


	private static String helpMessage() {
		return "There was trouble trying to publish bricks. See log for details.";
	}
	
}
