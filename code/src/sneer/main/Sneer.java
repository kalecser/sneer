package sneer.main;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Sneer {

	public static void main(String[] argsIgnored) throws Exception {
		new Sneer();
	}

	
	public Sneer() throws Exception {
		SneerVersionUpdater.installNewVersionIfPresent();
		independentClassLoader().loadClass("sneer.main.SneerSession").newInstance();
	}

	
	private URLClassLoader independentClassLoader() {
		ClassLoader noParent = null;
		return new URLClassLoader(classpath(), noParent);
	}
	
	private URL[] classpath() {
		URL[] langSupportJars = langSupportJars();
		URL[] classpath = new URL[langSupportJars.length + 2];
		classpath[0] = toURL(SneerFolders.OWN_BIN);
		classpath[1] = toURL(SneerCodeFolders.BIN);
		System.arraycopy(langSupportJars, 0, classpath, 2, langSupportJars.length);
		return classpath;
	}
	
	private URL[] langSupportJars() {
		List<URL> jarURLs = new ArrayList<URL>();
		LinkedList<File> folderQueue = new LinkedList<File>();
		folderQueue.add(SneerCodeFolders.LANG_LIB);
		while(!folderQueue.isEmpty()) {
			File curFolder = folderQueue.removeFirst();
			File[] curJars = curFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isFile() && file.getName().endsWith(".jar");
				}
			});
			for (File curJar : curJars) {
				jarURLs.add(toURL(curJar));
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
		return jarURLs.toArray(new URL[jarURLs.size()]);
	}
	
	private URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

}



