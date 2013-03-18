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
		return override != null
			? new File(override)
			: localRootFolder();
	}

	private static File localRootFolder() {
		File result = new File(SneerCodeFolders.class.getResource(".").getFile())
			.getParentFile()
			.getParentFile()
			.getParentFile()
			.getParentFile();
		assertChild(result, "code/bin");
		return result;
	}

	private static void assertChild(File parent, String child) {
		if (!new File(parent, child).exists())
			throw new IllegalStateException("Folder '" + parent + "' does not contain child " + "'" + child + "'");
	}

}
