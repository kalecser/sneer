package sneer.main;

import java.io.File;

public class SneerCodeFolders {

	public static final File SNEER_HOME = sneerHome();
	public static final File CODE                 = new File(SNEER_HOME, "code");
	public static final File PLATFORM_CODE        = new File(SNEER_HOME, "code/platform");
	public static final File PLATFORM_CODE_BACKUP = new File(SNEER_HOME, "code/backup");
	public static final File PLATFORM_CODE_STAGE  = new File(SNEER_HOME, "code/stage");

	
	private static File sneerHome() {
		String override = System.getProperty("sneer.home");
		if (override != null) return new File(override);

		return new File(System.getProperty("user.home"), "sneer");
	}

}
