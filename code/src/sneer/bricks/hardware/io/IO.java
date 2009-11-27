package sneer.bricks.hardware.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick
public interface IO {

	void crash(Closeable closeable);
	
	Files files();
	FileFilters fileFilters();
	Streams streams();
	
	interface FileFilters{
		Filter any();
		Filter not(Filter filter);
		Filter or(Filter... filters);
		Filter suffix(String sulfix);
		Filter name(String name);
		
		Collection<File> listFiles(File folder, Filter fileFilter, Filter folderFilter);
	}
	
	interface Filter{
		public boolean accept(File file);
		public boolean accept(File folder, String name);
	}
	
	interface Files{
		long sizeOfFolder(File Folder);

		Collection<File> listFiles(File folder, String[] extensions, boolean recursive);
		Collection<File> listFiles(File folder, Filter fileFilter, Filter folderFilter);
		
		void copyFile(File from, File to) throws IOException;
		void copyFileToFolder(File srcFile, File destFolder) throws IOException;
		void copyFolder(File srcFolder, File destFolder) throws IOException;
		void copyFolder(File srcFolder, File destFolder, Filter fileFilter) throws IOException;
		void forceDelete(File fileOrFolder) throws IOException;
		Iterator<File> iterate(File folder, String[] extensions, boolean recursive);

		void writeString(File file, String data) throws IOException;
		
		byte[] readBytes(File file) throws IOException;
		byte[] readBlock(File file, int blockNumber, int blockSize) throws IOException; // zero-based
		void readBytes(File file, Consumer<byte[]> content);
		void readBytes(File file, Consumer<byte[]> content, Consumer<IOException> exception);
		
		String readString(File file) throws IOException;
		void readString(File file, Consumer<String> content);
		void readString(File file, Consumer<String> content, Consumer<IOException> exception);
		
		void writeByteArrayToFile(File file, byte[] data) throws IOException;

		void assertSameContents(File file1, File file2) throws IOException;


	}
	
	interface Streams{
		String toString(InputStream input) throws IOException;
		byte[] toByteArray(InputStream input) throws IOException;
		byte[] readBytesAndClose(InputStream input) throws IOException;
		void closeQuietly(Closeable closeable);
	}
}