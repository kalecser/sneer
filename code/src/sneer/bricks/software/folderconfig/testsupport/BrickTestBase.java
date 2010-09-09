package sneer.bricks.software.folderconfig.testsupport;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.cpu.threads.tests.BrickTestWithThreads;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;


public abstract class BrickTestBase extends BrickTestWithThreads {

	{
		my(FolderConfig.class).storageFolder().set(new File(tmpFolderName(), "data"));
		my(FolderConfig.class).tmpFolder()    .set(new File(tmpFolderName(), "tmp" ));
	}

	protected void configureStorageFolder(Environment environment, final String folderName) {
		Environments.runWith(environment, new Closure() { @Override public void run() {
			my(FolderConfig.class).storageFolder().set(new File(tmpFolderName(), folderName));
		}});
	}

	protected void configureTmpFolder(Environment environment, final String folderName) {
		Environments.runWith(environment, new Closure() { @Override public void run() {
			my(FolderConfig.class).tmpFolder().set(new File(tmpFolderName(), folderName));
		}});
	}

}
