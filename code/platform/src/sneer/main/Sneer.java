package sneer.main;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
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
			toURL(SneerFolders.PLATFORM_BIN)
		};
	}

	private URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	
	
	private static void installStagedCodeIfNecessary() {
		//if (!stageFolder().exists()) return;
		//throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	private static File stageFolder() {
		return null;
		//return new SneerFolders
	}
	

	public static void installStagedCode(File stageFolder, File backupFolder, File codeFolder) throws IOException {
		copyDirectory(codeFolder, backupFolder, null);
		deleteCode(codeFolder);
		copyDirectory(stageFolder, codeFolder, new ExclusionFilter());
		deleteAtomically(stageFolder);
	}

	
	private static void deleteCode(File codeFolder) throws IOException {
		deleteFolder(codeFolder, new ExclusionFilter(
			existingFile(codeFolder, "src/sneer/main/Sneer.java"),
			existingFile(codeFolder, "bin/sneer/main/Sneer.class"),
			existingFile(codeFolder, "bin/sneer/main/ExclusionFilter.class"),
			existingFile(codeFolder, "src/sneer/main/SneerCodeFolders.java"),
			existingFile(codeFolder, "bin/sneer/main/SneerCodeFolders.class")
		));
	}

	
	
//////////////////////////////////////////////////////////// File Utils:	
	
	private static void deleteAtomically(File folder) throws IOException {
		File tmp = new File(folder.getParentFile(), "toBeDeleted");
		deleteFolder(tmp);
		if (!folder.renameTo(tmp)) throw new IOException("Unable to rename " + folder + " to " + tmp);
		deleteFolder(tmp);
	}

	
	private static void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
		    if (srcDir == null) {
		        throw new NullPointerException("Source must not be null");
		    }
		    if (destDir == null) {
		        throw new NullPointerException("Destination must not be null");
		    }
		    if (srcDir.exists() == false) {
		        throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
		    }
		    if (srcDir.isDirectory() == false) {
		        throw new IOException("Source '" + srcDir + "' exists but is not a directory");
		    }
		    if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
		        throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
		    }
		
		    doCopyDirectory(srcDir, destDir, filter);
		}
		
		private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
		    if (destDir.exists()) {
		        if (destDir.isDirectory() == false) {
		            throw new IOException("Destination '" + destDir + "' exists but is not a directory");
		        }
		    } else {
		        if (destDir.mkdirs() == false) {
		            throw new IOException("Destination '" + destDir + "' directory cannot be created");
		        }
		    }
		    if (destDir.canWrite() == false) {
		        throw new IOException("Destination '" + destDir + "' cannot be written to");
		    }
		    // recurse
		    File[] files = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
		    if (files == null) {  // null if security restricted
		        throw new IOException("Failed to list contents of " + srcDir);
		    }
		    for (int i = 0; i < files.length; i++) {
		        File copiedFile = new File(destDir, files[i].getName());
	            if (files[i].isDirectory()) {
	                doCopyDirectory(files[i], copiedFile, filter);
	            } else {
	                doCopyFile(files[i], copiedFile);
	            }
		    }
		    
            destDir.setLastModified(srcDir.lastModified());
		}

		
	    private static void doCopyFile(File srcFile, File destFile) throws IOException {
	        if (destFile.exists() && destFile.isDirectory()) {
	            throw new IOException("Destination '" + destFile + "' exists but is a directory");
	        }

	        FileInputStream input = new FileInputStream(srcFile);
	        try {
	            FileOutputStream output = new FileOutputStream(destFile);
	            try {
	                copy(input, output);
	            } finally {
	                closeQuietly(output);
	            }
	        } finally {
	            closeQuietly(input);
	        }

	        if (srcFile.length() != destFile.length()) {
	            throw new IOException("Failed to copy full contents from '" +
	                    srcFile + "' to '" + destFile + "'");
	        }
            destFile.setLastModified(srcFile.lastModified());
	    }

	    
		private static void copy(FileInputStream input, FileOutputStream output) throws IOException {
	        byte[] buffer = new byte[1024 * 4];
	        int n = 0;
	        while (-1 != (n = input.read(buffer)))
	            output.write(buffer, 0, n);
		}

		
		private static void closeQuietly(Closeable closable) {
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

}



class ExclusionFilter implements FileFilter {
	
	private final Collection<File> _filesToExclude;

	ExclusionFilter(File... filesToExclude) {
		_filesToExclude = new HashSet<File>(Arrays.asList(filesToExclude));
	}

	@Override public boolean accept(File candidate) {
		return !_filesToExclude.contains(candidate);
	}
	
}
