package sneer.installer;

import static sneer.main.SneerCodeFolders.BIN;
import static sneer.main.SneerCodeFolders.CODE;
import static sneer.main.SneerCodeFolders.SNEER_HOME;
import static sneer.main.SneerCodeFolders.SRC;
import static sneer.main.SneerFolders.LOG_FILE;
import static sneer.main.SneerFolders.OWN_CODE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

class Installation {

	private final URL _sneerJar = this.getClass().getResource("/sneer.jar");
	private final URL _ownJar = this.getClass().getResource("/own.jar");


	Installation() throws Exception {
		resetDirectories();
		updateCode();
		createOwnProjectIfNecessary();
	}


	private void resetDirectories() throws IOException {
		if(!SNEER_HOME.exists())
			SNEER_HOME.mkdirs();

		deleteFolder(SRC);
		deleteFolder(BIN);
		SRC.mkdirs();
		BIN.mkdirs();
	}

	
	private void deleteFolder(File folder) throws IOException {
		int tries = 10; //On Windows7 with AVG anti-virus apparently this is necessary. Might be necessary on other similar environments. Klaus July 2011 :(
		while (true) {
			if (tryToDeleteFolder(folder)) return;
			if (tries-- == 0) throw new IOException("Unable to delete after several attempts: " + folder);
			sleepForASecond();
		}
    }

	
	private boolean tryToDeleteFolder(File folder) throws IOException {
		if (!folder.exists()) return true;

        for (File entry : folder.listFiles())
			if (entry.isDirectory())
			    tryToDeleteFolder(entry);
			else
				entry.delete();
        
        return folder.delete();
	}

    
	private void updateCode() throws IOException {
		extractFiles(extractJar(_sneerJar, "sneer"), CODE);
	}

	
	private File extractJar(URL url, String prefix) throws IOException {
		File file =  File.createTempFile(prefix, "jar");
		file.deleteOnExit();

		InputStream input = url.openStream();
		IOUtils.copyToFile(input, file);
		input.close();
		return file;
	}

	
	private void extractFiles(File src, File toDir) throws IOException {
		if(!(src.exists()))
			throw new IOException("File '" + src.getAbsolutePath() + "' not found!");	

		FileInputStream inputStream = new FileInputStream(src);
		extractFiles(src, toDir, inputStream);
		inputStream.close();
	}

	
	private void extractFiles(File src, File toDir, FileInputStream inputStream) throws IOException {
		JarInputStream jis = new JarInputStream(inputStream);
		JarFile jar = new JarFile(src);
		JarEntry entry = null;

        while ((entry = jis.getNextJarEntry()) != null) {
        	File file = new File(toDir, entry.getName());

        	if(entry.isDirectory()) {
        		file.mkdirs();
				continue;
        	}
        	IOUtils.writeEntry(jar, entry, file);
        }
	}

	
	private void createOwnProjectIfNecessary() throws IOException {
		if(OWN_CODE.exists()) return;

		IOUtils.write(LOG_FILE, "jar file url: " + _ownJar.toString());
		File file = extractJar(_ownJar, "own");
		extractFiles(file, OWN_CODE.getParentFile());		
	}


	private void sleepForASecond() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}