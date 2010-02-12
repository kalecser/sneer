package sneer.main;

import java.io.File;

public class SneerCodeFolders {

	public static final File SNEER_HOME = sneerHome();

	public static final File CODE = new File(SNEER_HOME, "code");

	public static final File SRC = new File(CODE, "src");
	public static final File BIN = new File(CODE, "bin");
	public static final File STAGE = new File(CODE, "stage");
	
	private static File sneerHome() {
		String override = System.getProperty("sneer.home");
		if (override != null)
			return new File(override);

		return new File(System.getProperty("user.home"), "sneer");
	}

}
