package sneer.foundation.languagesupport;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LanguageJarFinder {

	public static File[] langSupportJarFiles(File root) {
		File langRoot = new File(root, "sneer/foundation/languagesupport");
		List<File> jarFiles = new ArrayList<File>();
		LinkedList<File> folderQueue = new LinkedList<File>();
		folderQueue.add(langRoot);
		while(!folderQueue.isEmpty()) {
			File curFolder = folderQueue.removeFirst();
			File[] curJars = curFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isFile() && file.getName().endsWith(".jar");
				}
			});
			for (File curJar : curJars) {
				jarFiles.add(curJar);
			}
			File[] subFolders = curFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory() && !file.getName().startsWith(".");
				}
			});
			for (File subFolder : subFolders) {
				folderQueue.add(subFolder);
			}
		}
		return jarFiles.toArray(new File[jarFiles.size()]);
	}

	public static URL[] langSupportJarURLs(File root) {
		File[] langSupportFiles = langSupportJarFiles(root);
		URL[] langSupportURLs = new URL[langSupportFiles.length];
		for (int fileIdx = 0; fileIdx < langSupportFiles.length; fileIdx++) {
			langSupportURLs[fileIdx] = toURL(langSupportFiles[fileIdx]);
		}
		return langSupportURLs;
	}
	
	private static URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

}
