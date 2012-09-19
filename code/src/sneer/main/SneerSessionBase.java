package sneer.main;

import static basis.environments.Environments.my;
import static sneer.main.SneerCodeFolders.BIN;
import static sneer.main.SneerCodeFolders.SNEER_HOME;
import static sneer.main.SneerCodeFolders.SRC;
import static sneer.main.SneerCodeFolders.STAGE;
import static sneer.main.SneerFolders.DATA;
import static sneer.main.SneerFolders.LOG_FILE;
import static sneer.main.SneerFolders.OWN_BIN;
import static sneer.main.SneerFolders.TMP;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.exceptions.robust.RobustExceptionLogging;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.snapps.system.log.file.LogToFile;
import sneer.bricks.snapps.system.log.sysout.LogToSysout;
import sneer.bricks.software.folderconfig.FolderConfig;
import basis.brickness.Brickness;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;

public abstract class SneerSessionBase {
	
	public SneerSessionBase() {
		Environments.runWith(container(), new Closure() { @Override public void run() {  //Who said Java doesn't have closures? XD
			prepare();
			start();
			my(Threads.class).waitUntilCrash();
		}});
	}
	

	protected abstract void start();

	
	private static Environment container() {
		return Brickness.newBrickContainer();
	}

	
	private void prepare() {
		setContextClassLoader();
		configure(my(FolderConfig.class));
		my(ClockTicker.class);
		startLogging();
	}

	
	private void setContextClassLoader() {
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
	}

	
	private void startLogging() {
		my(RobustExceptionLogging.class).turnOn();
		my(LogToSysout.class);
		my(LogToFile.class).startWritingLogTo(LOG_FILE);
	}

	
	private static void configure(FolderConfig dirs) {
		prepare(dirs.storageFolder(), DATA);
		prepare(dirs.tmpFolder(), TMP);
		prepare(dirs.ownBinFolder(), OWN_BIN);
		prepare(dirs.srcFolder(), SRC);
		prepare(dirs.binFolder(), BIN);
		prepare(dirs.gitFolder(), SNEER_HOME.toPath());
	
		dirs.stageFolder().set(STAGE);
	}

	
	private static void prepare(ImmutableReference<File> property, File folder) {
		if (!folder.exists() && !folder.mkdirs()) throw new IllegalStateException("Unable to create folder " + property);
		property.set(folder);
	}

	
	private static void prepare(ImmutableReference<Path> property, Path folder) {
		property.set(folder);
		if (Files.exists(folder)) return;
		
		try {
			Files.createDirectories(folder);
		} catch (IOException e) {
			 throw new IllegalStateException("Unable to create folder " + folder, e);
		}
	}

}