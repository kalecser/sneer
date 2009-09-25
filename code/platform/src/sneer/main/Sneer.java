package sneer.main;

import static sneer.main.SneerCodeFolders.PLATFORM_CODE;
import static sneer.main.SneerCodeFolders.PLATFORM_CODE_BACKUP;
import static sneer.main.SneerCodeFolders.PLATFORM_CODE_STAGE;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Sneer {


	public static void main(String[] argsIgnored) throws Exception {
		new Sneer();
	}
	
	public Sneer() throws Exception {
		installStagedCodeIfNecessary();
		independentClassLoader().loadClass("sneer.main.SneerSession").newInstance();
	}

	private URLClassLoader independentClassLoader() {
		ClassLoader noParent = null;
		return new URLClassLoader(classpath(), noParent);
	}

	private URL[] classpath() {
		return new URL[] {
			toURL(SneerFolders.OWN_BIN),
			toURL(SneerCodeFolders.PLATFORM_BIN)
		};
	}

	private URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	
	private static void installStagedCodeIfNecessary() throws IOException {
		installStagedCodeIfNecessary(PLATFORM_CODE_STAGE, newBackupFolder(), PLATFORM_CODE);
	}

	
	private static File newBackupFolder() {
		String now = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		File result = new File(PLATFORM_CODE_BACKUP, now);
		if (!result.mkdirs()) throw new IllegalStateException("Unable to mkdirs: " + result);
		return result;
	}


	public static void installStagedCodeIfNecessary(File stageFolder, File backupFolder, File currentFolder) throws IOException {
		if (!stageFolder.exists()) return;
		
		copyFolder(currentFolder, backupFolder, null);
		ExclusionFilter filesToPreserve = exclusionFilter(currentFolder);
		deleteFolder(currentFolder, filesToPreserve);
		copyFolder(stageFolder, currentFolder, filesToPreserve);
		deleteAtomically(stageFolder);
	}

	
	private static ExclusionFilter exclusionFilter(File currentFolder) {
		return new ExclusionFilter(
			existingFile(currentFolder, "src/sneer/main/Sneer.java"),
			existingFile(currentFolder, "bin/sneer/main/Sneer.class"),
			existingFile(currentFolder, "bin/sneer/main/Sneer$ExclusionFilter.class"),
			
			existingFile(currentFolder, "src/sneer/main/SneerCodeFolders.java"),
			existingFile(currentFolder, "bin/sneer/main/SneerCodeFolders.class")
		);
	}

	
	
//////////////////////////////////////////////////////////// File Utils:	
	
	private static void deleteAtomically(File folder) throws IOException {
		File tmp = new File(folder.getParentFile(), "toBeDeleted");
		deleteFolder(tmp);
		if (!folder.renameTo(tmp)) throw new IOException("Unable to rename " + folder + " to " + tmp);
		deleteFolder(tmp);
	}

	
	private static void copyFolder(File original, File copy, FileFilter filter) throws IOException {
		if (!copy.exists() && !copy.mkdirs()) throw new IOException("Unable to create: " + copy);

		File[] files = filter == null
			? original.listFiles()
			: original.listFiles(filter);

		for (File entry : files) {
			File entryCopy = new File(copy, entry.getName());
			if (entry.isDirectory()) {
				copyFolder(entry, entryCopy, filter);
			} else {
				copyFile(entry, entryCopy);
			}
		}

		copy.setLastModified(original.lastModified());
	}

	private static void copyFile(File original, File copy)	throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(original);
			out = new FileOutputStream(copy);
			pipe(in, out);
		} finally {
			close(in);
			close(out);
		}

		if (copy.length() != original.length())
			throw new IllegalStateException("Files should be the same length. Original: " + original + " copy: " + copy);
		
		copy.setLastModified(original.lastModified());
	}

    
	private static void pipe(FileInputStream in, FileOutputStream out) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int bytesRead = 0;
        while ((bytesRead = in.read(buffer)) != -1)
            out.write(buffer, 0, bytesRead);
	}

	
	private static void close(Closeable closable) {
		if (closable == null) return;
		try { closable.close(); } catch (IOException e) {}
	}

	
	private static void deleteFolder(File folder) throws IOException {
        deleteFolder(folder, null);
    }

	
	private static boolean deleteFolder(File folder, FileFilter filter) throws IOException {
		if (!folder.exists()) return true;
		
		boolean isEmpty = true;

        for (File file : folder.listFiles())
        	if (!deleteFileOrFolder(file, filter))
        		isEmpty = false;
        
        if (!isEmpty) return false;
        
        if (!folder.delete())
        	throw new IOException(("Unable to delete folder " + folder + "."));
        
        return true;
	}		
	
	
    private static boolean deleteFileOrFolder(File file, FileFilter filter) throws IOException {
    	if (filter != null && !filter.accept(file)) return false;
    	
        if (file.isDirectory())
            return deleteFolder(file, filter);
        
        if (!file.delete())
        	throw new IOException(("Unable to delete file: " + file));
        
        return true;
    }

    
	private static File existingFile(File folder, String fileName) {
		File result = new File(folder, fileName);
		if (!result.exists()) throw new IllegalStateException("File should exist: " + result);
		return result;
	}

	
	private static class ExclusionFilter implements FileFilter {
		
		private final Collection<File> _filesToExclude;

		ExclusionFilter(File... filesToExclude) {
			_filesToExclude = new HashSet<File>(Arrays.asList(filesToExclude));
		}

		@Override public boolean accept(File candidate) {
			return !_filesToExclude.contains(candidate);
		}
		
	}

}



