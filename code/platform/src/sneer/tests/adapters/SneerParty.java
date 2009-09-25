package sneer.tests.adapters;

import java.io.File;

import sneer.tests.SovereignParty;

public interface SneerParty extends SovereignParty {

	void configDirectories(File dataFolder, File tmpFolder, File currentCodeFolder, File platformSrcFolder, File platformBinFolder, File stageFolder, File backupFolder);
	void setSneerPort(int port);
	int sneerPort();

	void connectTo(SneerParty b);
	
	void start();
	void crash();

}
