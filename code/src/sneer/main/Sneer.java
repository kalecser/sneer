package sneer.main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
		List<URL> result = new ArrayList<URL>();
		result.add(toURL(SneerFolders.OWN_BIN));
		result.add(toURL(SneerCodeFolders.BIN));
//		for (URL jar : langSupportJars())
//			result.add(jar);
		return result.toArray(new URL[0]);
	}


//	private URL[] langSupportJars() {
//		return JarFinder.languageSupportJars(SneerCodeFolders.BIN);
//	}
	
	
	private URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

}



