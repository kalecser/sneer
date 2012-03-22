package sneer.bricks.software.folderconfig.testsupport;

import static basis.environments.Environments.my;

import java.io.File;

import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;

import sneer.bricks.hardware.cpu.threads.tests.BrickTestWithThreads;
import sneer.bricks.software.folderconfig.FolderConfig;


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
