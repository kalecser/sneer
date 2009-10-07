package sneer.main;

import static sneer.main.SneerCodeFolders.CODE;
import static sneer.main.SneerCodeFolders.SNEER_HOME;

import java.io.File;

public class SneerFolders {

	public static final File DATA 			= new File(SNEER_HOME, dummyPrefix() + "data");
	public static final File TMP 			= new File(SNEER_HOME, dummyPrefix() + "tmp");
	public static final File LOG_FILE 		= new File(SNEER_HOME, dummyPrefix() + "logs/log.txt");
	public static final File OWN_CODE 		= new File(CODE, "own");
	public static final File OWN_BIN 		= new File(CODE, "own/bin");

	
	private static String dummyPrefix() {
		return "true".equals(System.getProperty("sneer.dummy"))
			? "dummy"
			: "";
	}

}
