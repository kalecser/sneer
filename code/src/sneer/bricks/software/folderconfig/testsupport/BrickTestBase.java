package sneer.bricks.software.folderconfig.testsupport;

import static basis.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;

import sneer.bricks.hardware.cpu.threads.tests.BrickTestWithThreads;
import sneer.bricks.software.folderconfig.FolderConfig;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;


public abstract class BrickTestBase extends BrickTestWithThreads {
	
	private final List<Environment> environments = new ArrayList<Environment>();

	{
		my(FolderConfig.class).storageFolder().set(new File(tmpFolderName(), "data"));
		my(FolderConfig.class).tmpFolder()    .set(new File(tmpFolderName(), "tmp" ));
	}
	
	@After
	public void afterBrickTestBase() {
		for (Environment env : environments)
			crash(env);
		
		environments.clear();
	}
	
	protected Environment newSpecialTestEnvironment(Object... bindings) {
		Environment ret = super.newTestEnvironment(bindings);
		configureStorageFolder(ret, "environmentData" + environments.size());
		configureTmpFolder(ret, "environmentTmp" + environments.size());
		environments.add(ret);
		return ret;
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
