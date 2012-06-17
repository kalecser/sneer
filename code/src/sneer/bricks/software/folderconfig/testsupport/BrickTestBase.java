package sneer.bricks.software.folderconfig.testsupport;

import static basis.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;

import sneer.bricks.hardware.cpu.threads.tests.BrickTestWithThreads;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.software.folderconfig.FolderConfig;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;


public abstract class BrickTestBase extends BrickTestWithThreads {
	
	private final List<Environment> environments = new ArrayList<Environment>();

	{
		configureStorageFoldersIfNecessary();
	}
	
	@After
	public void afterBrickTestBase() {
		for (Environment env : environments)
			crash(env);
		
		environments.clear();
	}
	
	@Override
	protected Environment newTestEnvironment(Object... bindings) {
		Environment ret = super.newTestEnvironment(bindings);
		Environments.runWith(ret, new Closure() { @Override public void run() {
			configureStorageFoldersIfNecessary();
		}});
		environments.add(ret);
		return ret;
	}

	
	private void configureStorageFoldersIfNecessary() {
		configureIfNecessary(my(FolderConfig.class).storageFolder(), "data");
		configureIfNecessary(my(FolderConfig.class).tmpFolder(), "tmp");
	}

	
	private void configureIfNecessary(ImmutableReference<File> folder, String folderName) {
		if(folder.isAlreadySet()) return;
		folder.set(new File(tmpFolderName(), folderName + currentEnvironmentLabel()));
	}

	
	private int currentEnvironmentLabel() {
		return environments.size();
	}

}
