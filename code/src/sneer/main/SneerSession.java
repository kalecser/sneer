package sneer.main;

import static sneer.foundation.environments.Environments.my;
import static sneer.main.SneerCodeFolders.BIN;
import static sneer.main.SneerCodeFolders.STAGE;
import static sneer.main.SneerCodeFolders.SRC;
import static sneer.main.SneerFolders.DATA;
import static sneer.main.SneerFolders.LOG_FILE;
import static sneer.main.SneerFolders.OWN_BIN;
import static sneer.main.SneerFolders.TMP;

import java.io.File;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.exceptions.robust.RobustExceptionLogging;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.identity.keys.gui.PublicKeyDialog;
import sneer.bricks.snapps.system.log.file.LogToFile;
import sneer.bricks.snapps.system.log.sysout.LogToSysout;
import sneer.bricks.software.bricks.snappstarter.SnappStarter;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.brickness.Brickness;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class SneerSession {
	
	public SneerSession() {
		Environments.runWith(container(), new Closure() { @Override public void run() {  //Who said Java doesn't have closures? XD
			start();
		}});
	}

	
	private void start() {
		setContextClassLoader();
		configure(my(FolderConfig.class));
		startLogging();
		my(PublicKeyDialog.class).initPublicKeyIfNecessary();
		my(SnappStarter.class).startSnapps();
		my(Threads.class).waitUntilCrash();
	}


	private void setContextClassLoader() {
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
	}

	
	private void startLogging() {
		my(RobustExceptionLogging.class).turnOn();
		my(LogToSysout.class);
		my(LogToFile.class).startWritingLogTo(LOG_FILE);
	}

	
	private static Environment container() {
		return Brickness.newBrickContainer();
	}
	
	
	private static void configure(FolderConfig dirs) {
		createAndSet(dirs.storageFolder(), DATA);
		createAndSet(dirs.tmpFolder(), TMP);
		createAndSet(dirs.ownBinFolder(), OWN_BIN);
		createAndSet(dirs.srcFolder(), SRC);
		createAndSet(dirs.binFolder(), BIN);

		dirs.stageFolder().set(STAGE);
	}

	
	private static void createAndSet(ImmutableReference<File> property, File folder) {
		if (!folder.exists() && !folder.mkdirs()) throw new IllegalStateException("Unable to create folder " + property);
		property.set(folder);
	}

}
