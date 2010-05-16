package sneer.bricks.hardware.io.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;

class IOImpl implements IO {
	
	private Files _files = new Files(){
		@Override public int fileSizeInBlocks(long fileSizeInBytes, int blockSize) { return (fileSizeInBytes == 0) ? 0 : (int) ((fileSizeInBytes - 1) / blockSize) + 1; }
		@Override public long sizeOfFolder(File folder) { return FileUtils.sizeOfDirectory(folder); }

		@Override public void copyFolder(File srcFolder, File destFolder, Filter fileFilter) throws IOException { FolderCopierToWorkaroundCommonsIoBug.copyDirectory(srcFolder, destFolder, asIOFileFilter(fileFilter), true); }
		@Override public void copyFolder(File srcFolder, File destFolder) throws IOException { FolderCopierToWorkaroundCommonsIoBug.copyDirectory(srcFolder, destFolder, null, true); }
		@Override public Collection<File> listFiles(File folder, String[] extensions, boolean recursive) { return FileUtils.listFiles(folder, extensions, recursive); }
		@Override public Collection<File> listFiles(File folder, Filter fileFilter, Filter folderFilter) { return FileUtils.listFiles(folder, asIOFileFilter(fileFilter), asIOFileFilter(folderFilter)); }
		@Override public void writeString(File file, String data) throws IOException { FileUtils.writeStringToFile(file, data); }
		
		@Override public void forceDelete(File fileOrFolder) throws IOException {
			if (!fileOrFolder.exists()) return;
			FileUtils.forceDelete(fileOrFolder);
		}

		@Override public void forceDeleteOnExit(File fileOrFolder) throws IOException {
			FileUtils.forceDeleteOnExit(fileOrFolder);
		}

		@Override public Iterator<File> iterate(File folder, String[] extensions, boolean recursive){ return FileUtils.iterateFiles(folder, extensions, recursive); }
		
		@Override public String readString(File file) throws IOException {return new String(readBytes(file)); }
		@Override public void readString(final File file, final Consumer<String> content) { readBytes(file, new Consumer<byte[]>(){ @Override public void consume(byte[] value) { content.consume(new String(value)); }});}
		@Override public void readString(final File file, final Consumer<String> content, final Consumer<IOException> exception) {
			readBytes(file, new Consumer<byte[]>(){ @Override public void consume(byte[] value) {  content.consume(new String(value)); } }, exception);}

		@Override public byte[] readBytes(File file) throws IOException {  return FileUtils.readFileToByteArray(file);  }
		@Override public void readBytes(final File file, Consumer<byte[]> content) {
			readBytes(file, content, new Consumer<IOException>(){ @Override public void consume(IOException exception) {
				my(BlinkingLights.class).turnOn(LightType.ERROR, "Error", "Unable to read file: " + file.getAbsolutePath(),  exception, 5*60*1000);
			}});		
		}

		@Override
		public byte[] readBlock(File file, int blockNumber, int blockSize) throws IOException {
			final long position = blockNumber * blockSize;
			int bytesToRead = (int) Math.min(blockSize, file.length() - position);
			if (bytesToRead < 1) throw new EOFException("Attempt to read position: " + position + ", File length: " + file.length());
			
			final byte[] result = new byte[bytesToRead];
			RandomAccessFile rFile = new RandomAccessFile(file, "r");
			try {
				rFile.seek(position);
				rFile.readFully(result);
				return result;
			} finally {
				rFile.close();
			}
		}

		@Override public void writeByteArrayToFile(File file, byte[] data) throws IOException { FileUtils.writeByteArrayToFile(file, data); }
		
		@Override public void assertSameContents(File file1, File file2) throws IOException {
			if (file1.isDirectory() != file2.isDirectory())
				throw new IllegalStateException(file1 + " is a " + type(file1) + " but " + file2 + " is a " + type(file2));
			
			if (file1.isDirectory())
				assertFolderContentEquals(file1, file2);
			else
				if (!FileUtils.contentEquals(file1, file2))
					throw new IllegalStateException(file1 + " != " + file2);
			
		}

		private String type(File file) {
			return file.isDirectory() ? "folder" : "file";
		}
		private void assertFolderContentEquals(File folder1, File folder2) throws IOException {
			File[] files1 = sortedFiles(folder1);
			File[] files2 = sortedFiles(folder2);
			
			if (files1.length != files2.length)
				throw new IllegalStateException("Different number of files in: '" + folder1 + "' and '" + folder2 + "'");
			
			for (int i = 0; i < files1.length; i++)
				assertFolderEntryEquals(files1[i], files2[i]);
		}

		private void assertFolderEntryEquals(File file1, File file2) throws IOException {
			assertNameEquals(file1, file2);
			assertDateEquals(file1, file2);
			assertSameContents(file1, file2);
		}

		private void assertDateEquals(File file1, File file2) {
			if (file1.isDirectory()) return; // We don't care about directory dates.
			if (file1.lastModified() != file2.lastModified())
				throw new IllegalStateException("Different modification dates for: '" + file1 + "' and '" + file2 + "'");
		}

		private void assertNameEquals(File file1, File file2) {
			if (!file1.getName().equals(file2.getName()))
				throw new IllegalStateException("Expecting '" + file1 + "' but got '" + file2 + "'");
		}

		private File[] sortedFiles(File folder) {
			File[] result = folder.listFiles();
			if (result == null) result = new File[0];
			Arrays.sort(result);
			return result;
		}

		@Override public void readBytes(File file, Consumer<byte[]> content, Consumer<IOException> exception) {
			try {
				content.consume(readBytes(file));
			} catch (IOException e) {
				exception.consume(e);
			}
		}
		@Override
		public void copyFile(File from, File to) throws IOException {
			FileUtils.copyFile(from, to);
		}

		@Override
		public void copyFileToFolder(File srcFile, File destFolder) throws IOException {
			FileUtils.copyFileToDirectory(srcFile, destFolder, false);
		}

	};
	
	private Streams _streams = new Streams(){
		@Override public String toString(InputStream input) throws IOException { return IOUtils.toString(input); }
		@Override public byte[] toByteArray(InputStream input) throws IOException { return IOUtils.toByteArray(input); }
		@Override public byte[] readBytesAndClose(InputStream input) throws IOException { 
			try {
				return toByteArray(input);
			}finally{
				try { input.close(); } catch (Throwable ignore) { }
			}
		}
	};

	private FileFilters _fileFilters = new FileFilters(){
		@Override public Filter any() { return adapt(FileFilterUtils.trueFileFilter()); }
		@Override public Filter none() { return adapt(FileFilterUtils.falseFileFilter()); }
		@Override public Filter not(Filter filter) { return adapt(FileFilterUtils.notFileFilter(asIOFileFilter(filter))); }
		@Override public Filter name(String name) { return adapt(FileFilterUtils.nameFileFilter(name)); }
		@Override public Filter suffix(String sulfix) { return adapt(FileFilterUtils.suffixFileFilter(sulfix)); }
		@Override public Filter or(Filter[] filters) {
			if (filters.length < 1) throw new IllegalArgumentException("The array of filters cannot be empty");
			
			IOFileFilter current = asIOFileFilter(filters[0]);
			for (int i = 1; i < filters.length; i++) {
				IOFileFilter filter = asIOFileFilter(filters[i]);
				current = FileFilterUtils.orFileFilter(current, filter);
			}
			return adapt(current);
		}

		@Override public Collection<File> listFiles(File folder, Filter fileFilter, Filter dirFilter) { return FileUtils.listFiles(folder, asIOFileFilter(fileFilter), asIOFileFilter(dirFilter)); }
		
		private Filter adapt(IOFileFilter filter) { return new IOFileFilterAdapter(filter); }
		
		class IOFileFilterAdapter implements IOFileFilter, Filter {
			IOFileFilter _delegate;
			public IOFileFilterAdapter(IOFileFilter delegate) { _delegate = delegate; }
			@Override public boolean accept(File file) { return _delegate.accept(file);}
			@Override public boolean accept(File dir, String name) { return _delegate.accept(dir, name); }
		}

		@Override public Filter foldersAndExtensions(String... acceptedExtensions) {
			Collection<Filter> fileExtensionFilters = my(CollectionUtils.class).map(Arrays.asList(acceptedExtensions), new Functor<String, Filter>() { @Override public Filter evaluate(String acceptedExtension) throws RuntimeException {
				return adapt(FileFilterUtils.suffixFileFilter(acceptedExtension));
			}});
			fileExtensionFilters.add(adapt(FileFilterUtils.directoryFileFilter()));

			return or(fileExtensionFilters.toArray(new Filter[0]));  
		}

	};
	
	@Override public Files files() { return _files; }
	@Override public Streams streams() { return _streams; }
	@Override public FileFilters fileFilters() { return _fileFilters; }

	@Override public void crash(Closeable closeable) {
		if (closeable == null) return;
		try { closeable.close(); } catch (IOException ignored) {}
	}

	private static IOFileFilter asIOFileFilter(final Filter filter) {
		if (filter == null)
			return null;
		
		if (filter instanceof IOFileFilter)
			return (IOFileFilter)filter;
		
		return new IOFileFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return filter.accept(dir, name);
			}
			
			@Override
			public boolean accept(File file) {
				return filter.accept(file);
			}
		};
	}
	
	
	@Override
	public Filenames filenames() {
		return new Filenames() {
			@Override public String separatorsToUnix(String path) { return FilenameUtils.separatorsToUnix(path); }
		};
	}
	
}