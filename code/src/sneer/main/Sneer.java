package sneer.main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import sneer.foundation.languagesupport.LanguageJarFinder;

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
		URL[] langSupportJars = LanguageJarFinder.langSupportJarURLs(SneerCodeFolders.BIN);
		URL[] classpath = new URL[langSupportJars.length + 2];
		classpath[0] = toURL(SneerFolders.OWN_BIN);
		classpath[1] = toURL(SneerCodeFolders.BIN);
		System.arraycopy(langSupportJars, 0, classpath, 2, langSupportJars.length);
		return classpath;
	}
	
	private URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

}



