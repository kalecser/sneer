package sneer.bricks.hardware.io.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

@SuppressWarnings("unchecked")
public class FolderCopierToWorkaroundCommonsIoBug {
	static void copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate) throws IOException {
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
	
	    // Cater for destination being directory within the source directory (see IO-141)
	    List exclusionList = null;
	    if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
	        File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
	        if (srcFiles != null && srcFiles.length > 0) {
	            exclusionList = new ArrayList(srcFiles.length);
	            for (int i = 0; i < srcFiles.length; i++) {
	                File copiedFile = new File(destDir, srcFiles[i].getName());
	                exclusionList.add(copiedFile.getCanonicalPath());
	            }
	        }
	    }
	    doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
	}
	
	private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter,
	        boolean preserveFileDate, List exclusionList) throws IOException {
	    if (destDir.exists()) {
	        if (destDir.isDirectory() == false) {
	            throw new IOException("Destination '" + destDir + "' exists but is not a directory");
	        }
	    } else {
	        if (destDir.mkdirs() == false) {
	            throw new IOException("Destination '" + destDir + "' directory cannot be created");
	        }

	        
// This was being done too early. Further writes to destDir will change its lastModified date: 
//	        if (preserveFileDate) {
//	            destDir.setLastModified(srcDir.lastModified());
//	        }
	        
	        
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
	        if (exclusionList == null || !exclusionList.contains(files[i].getCanonicalPath())) {
	            if (files[i].isDirectory()) {
	                doCopyDirectory(files[i], copiedFile, filter, preserveFileDate, exclusionList);
	            } else {
	                doCopyFile(files[i], copiedFile, preserveFileDate);
	            }
	        }
	    }
	    
        //LastModified date has to be set here: 
        if (preserveFileDate) {
            destDir.setLastModified(srcDir.lastModified());
        }

	}
	
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream input = new FileInputStream(srcFile);
        try {
            FileOutputStream output = new FileOutputStream(destFile);
            try {
                IOUtils.copy(input, output);
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(input);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

	
}

