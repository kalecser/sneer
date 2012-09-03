package sneer.tests.adapters;

import java.io.File;
import java.nio.file.Path;

import sneer.tests.SovereignParty;

public interface SneerParty extends SovereignParty {

	void configDirectories(File dataFolder, File tmpFolder, File currentCodeFolder, File srcFolder, File binFolder, File stageFolder, Path gitFolder);
	void setSneerPort(int port);
	int sneerPort();

	void startConnectingTo(SneerParty other);
	void acceptConnectionFrom(String otherName);
	void waitUntilOnline(SneerParty other);
	
	void start(String name, int port);
	void crash();

	void startStunServer();
}
