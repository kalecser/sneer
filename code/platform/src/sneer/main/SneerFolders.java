package sneer.main;

import static sneer.main.SneerCodeFolders.CODE;
import static sneer.main.SneerCodeFolders.PLATFORM_CODE;
import static sneer.main.SneerCodeFolders.SNEER_HOME;

import java.io.File;

public class SneerFolders {

	protected static final File DATA 			= new File(SNEER_HOME, dummyPrefix() + "data");
	protected static final File TMP 			= new File(SNEER_HOME, dummyPrefix() + "tmp");
	protected static final File LOG_FILE 		= new File(SNEER_HOME, dummyPrefix() + "logs/log.txt");
	protected static final File OWN_CODE 		= new File(CODE, "own");
	protected static final File OWN_BIN 		= new File(CODE, "own/bin");
	protected static final File PLATFORM_SRC 	= new File(PLATFORM_CODE, "src");
	protected static final File PLATFORM_BIN 	= new File(PLATFORM_CODE, "bin");

	
	private static String dummyPrefix() {
		return "true".equals(System.getProperty("sneer.dummy"))
			? "dummy"
			: "";
	}

}
