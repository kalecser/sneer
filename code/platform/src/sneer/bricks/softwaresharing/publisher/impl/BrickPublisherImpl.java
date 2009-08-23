package sneer.bricks.softwaresharing.publisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.publisher.FilePublisher;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.publisher.BrickPublisher;
import sneer.bricks.softwaresharing.publisher.SrcFolderHash;

class BrickPublisherImpl implements BrickPublisher {
	
	private final Light _errorLight = my(BlinkingLights.class).prepare(LightType.ERROR);

	@Override
	public void publishAllBricks() {
		
		publishAllBricks(platformSrcFolder());
		
	}


	private File platformSrcFolder() {
		return my(FolderConfig.class).platformSrcFolder().get();
	}


	private void publishAllBricks(File srcFolder) {
		Sneer1024 hash;
		try {
			hash = my(FilePublisher.class).publish(srcFolder);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(_errorLight, "Error publishing bricks.", helpMessage(), e);
			return;
		}
		
		my(Logger.class).log("Publishing " + SrcFolderHash.class.getSimpleName());
		my(TupleSpace.class).publish(new SrcFolderHash(hash));
	}

	private static String helpMessage() {
		return "There was trouble trying to publish bricks. See log for details.";
	}
	
//
//
//	private String packageFor(String brickName) {
//		return my(Lang.class).strings().substringBeforeLast(brickName, ".");
//	}

}
