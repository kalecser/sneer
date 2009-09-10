package sneer.bricks.software.folderconfig;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.cpu.threads.tests.BrickTestWithThreads;


public abstract class BrickTest extends BrickTestWithThreads {

	{
		my(FolderConfig.class).storageFolder().set(new File(tmpFolderName(), "data"));
		my(FolderConfig.class).tmpFolder().set(new File(tmpFolderName(), "tmp"));
	}
	
}
