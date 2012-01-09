package sneer.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class IOUtils {
	
	static void write(File file, byte[] bytes) throws IOException {
		file.getParentFile().mkdirs();
		file.createNewFile();
        OutputStream out = new java.io.FileOutputStream(file);
        try {
            out.write(bytes);
        } finally {
            try { out.close(); } catch (Throwable ignore) {}
        }	
	}
	
	static void write(File file, String text) throws IOException {
         write(file, text.getBytes());
	}	
	

	static void copyToFile(InputStream input, File file) throws IOException {
		file.getParentFile().mkdirs();
		file.createNewFile();
		
		OutputStream output = new java.io.FileOutputStream(file);
		try {
		    byte[] buffer = new byte[1024 * 4];
			int n = 0;
			while (-1 != (n = input.read(buffer)))
			    output.write(buffer, 0, n);
        } finally {
            try { output.close(); } catch (Throwable ignore) {}
        }	
	}

	static void writeEntry(JarFile jar, JarEntry entry, File file) throws IOException {
		final InputStream is = jar.getInputStream(entry);
		FileOutputStream output = new FileOutputStream(file);
		try {
			byte[] buffer = new byte[1024];
			
			int n = 0;
			while (-1 != (n = is.read(buffer)))
				output.write(buffer, 0, n);
			output.close();
			file.setLastModified(entry.getTime());
		} finally {
			try { is.close(); } catch (Throwable ignore) { }
		}		
	}
}