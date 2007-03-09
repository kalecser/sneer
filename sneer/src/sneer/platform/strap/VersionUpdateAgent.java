package sneer.platform.strap;

import static sneer.platform.SneerDirectories.latestInstalledPlatformJar;
import static sneer.platform.SneerDirectories.validNumber;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import sneer.platform.TestMode;
import sneer.platform.server.Agent;
import sneer.platform.server.Command;
import sneer.platform.server.ServerConfig;
import wheel.io.Log;

public class VersionUpdateAgent implements Agent {

	public VersionUpdateAgent(int requestedVersion) {
		_requestedVersion = requestedVersion;
	}

	
	private final int _requestedVersion;
	
		
	public void helpYourself(ObjectInputStream ignored, ObjectOutputStream objectOut) throws Exception {
		objectOut.writeObject(updateCommand());
		objectOut.flush();
		Log.log("Done.");
		if (TestMode.isInTestMode()) exitAfterAWhile();
	}


	private void exitAfterAWhile() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		System.exit(0);
	}

	
	private Command updateCommand() throws IOException {
		File mainApp = newestMainApp();
		if (mainApp == null) {
			Log.log("No mainApp files found.");
			return noNewVersionCommand();
		}
		
		int newestVersion = validNumber(mainApp.getName());
		if (_requestedVersion > newestVersion) {
			Log.log("Up to date. Version " + newestVersion);
			return noNewVersionCommand();
		}
		
		Log.log("Uploading " + mainApp.getName() + "...");
		return new VersionUpdateCommand(version(mainApp), contents(mainApp));
	}


	private LogMessage noNewVersionCommand() {
		return new LogMessage("Não há atualização nova para o Sneer.");
	}

		
	private static int version(File mainApp) {
		return validNumber(mainApp.getName());
	}

	
	private static File newestMainApp() {
		return latestInstalledPlatformJar(ServerConfig.MAIN_APP_DIRECTORY);
	}

	
	private static byte[] contents(File mainApp) throws IOException {
		DataInputStream dataIn = new DataInputStream(new FileInputStream(mainApp));
		byte[] result = new byte[dataIn.available()];
		dataIn.readFully(result);
		return result;
	}

	private static final long serialVersionUID = 1L;

}
