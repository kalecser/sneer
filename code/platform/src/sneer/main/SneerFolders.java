package sneer.main;

import static sneer.main.SneerCodeFolders.SNEER_HOME;

import java.io.File;

public class SneerFolders {

	protected static final File DATA 			= new File(SNEER_HOME, dummyPrefix() + "data");
	protected static final File TMP 			= new File(SNEER_HOME, dummyPrefix() + "tmp");
	protected static final File LOG_FILE 		= new File(SNEER_HOME, dummyPrefix() + "logs/log.txt");
	protected static final File OWN_CODE 		= new File(SNEER_HOME, "code/own");
	protected static final File OWN_BIN 		= new File(SNEER_HOME, "code/own/bin");
	protected static final File PLATFORM_CODE 	= new File(SNEER_HOME, "code/platform");
	protected static final File PLATFORM_SRC 	= new File(SNEER_HOME, "code/platform/src");
	protected static final File PLATFORM_BIN 	= new File(SNEER_HOME, "code/platform/bin");

	
	private static String dummyPrefix() {
		return "true".equals(System.getProperty("sneer.dummy"))
			? "dummy"
			: "";
	}

}
