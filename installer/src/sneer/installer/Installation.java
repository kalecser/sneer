package sneer.installer;

import static sneer.main.SneerCodeFolders.BIN;
import static sneer.main.SneerCodeFolders.CODE;
import static sneer.main.SneerCodeFolders.SNEER_HOME;
import static sneer.main.SneerCodeFolders.SRC;
import static sneer.main.SneerFolders.LOG_FILE;
import static sneer.main.SneerFolders.OWN_CODE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

class Installation {

	private final URL _sneerJar = this.getClass().getResource("/sneer.jar");
	private final URL _ownJar = this.getClass().getResource("/own.jar");

	private JWindow _splashScreen;

	Installation() throws Exception {
		showSplashScreenIfNecessary();
		resetDirectories();
		updateCode();
		createOwnProjectIfNecessary();
		closeSplashScreenIfNecessary();
	}

	private void showSplashScreenIfNecessary() {
		if (!showSplashScreen()) return; 
		
		_splashScreen = new JWindow();
		Image image = Toolkit.getDefaultToolkit().createImage(Installation.class.getResource("dogfood.png"));
		ImageIcon icon = new ImageIcon(image);
		_splashScreen.setLayout(new BorderLayout());
		_splashScreen.add(new JLabel(icon), BorderLayout.CENTER);

		int imgWidth = 600;
		int imgHeight = 300;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
		Point basePoint = new Point(
			(int) ((screenSize.getWidth() - imgWidth) / 2), 
			(int) ((screenSize.getHeight() - imgHeight) / 2)
		);

		_splashScreen.setBounds(basePoint.x, basePoint.y, imgWidth, imgHeight);
		_splashScreen.setVisible(true);
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

	private void closeSplashScreenIfNecessary() {
		if (!showSplashScreen()) return; 
		_splashScreen.setVisible(false);
		_splashScreen.dispose();
	}

	
	private void sleepForASecond() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	private boolean showSplashScreen() {
		String parameter = System.getProperty("sneer.splash", "no");
		return parameter.toLowerCase().equals("yes");
	}
	
}