package sneer.installer;

import static sneer.main.SneerCodeFolders.PLATFORM_BIN;
import static sneer.main.SneerCodeFolders.PLATFORM_CODE;
import static sneer.main.SneerCodeFolders.SNEER_HOME;

import java.io.File;

import sneer.main.SneerFolders;

public class Folders extends SneerFolders {

	static File SNEER_HOME() { return SNEER_HOME; }
	static File DATA() { return DATA; }
	static File LOG_FILE() { return LOG_FILE; }
	static File OWN_CODE() { return OWN_CODE; }
	static File OWN_BIN() { return OWN_BIN; }
	static File PLATFORM_CODE() { return PLATFORM_CODE; }
	static File PLATFORM_BIN() { return PLATFORM_BIN; }
	
}
