package sneer.bricks.hardwaresharing.files.reader.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.cache.FileCache;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.reader.FileReader;
import sneer.bricks.pulp.crypto.Sneer1024;

class FileReaderImpl implements FileReader {

	private static final FileCache FileCache = my(FileCache.class);


	@Override
	public Sneer1024 readIntoTheFileCache(File fileOrFolder) throws IOException {
		return (fileOrFolder.isDirectory())
			? readFolder(fileOrFolder)
			: readFile(fileOrFolder);
	}


	private static Sneer1024 readFile(File file) throws IOException {
		long length = file.length();
		return length > FileReader.MAXIMUM_FILE_BLOCK_SIZE
			? BigFileReader.readFile(file)
			: FileCache.putFileContents(readFileContents(file));
	}


	private Sneer1024 readFolder(File folder) throws IOException {
		return FileCache.putFolderContents(
			new FolderContents(immutable(
				readEachFolderEntry(folder)
			))
		);
	}

	
	private List<FileOrFolder> readEachFolderEntry(File folder) throws IOException {
		List<FileOrFolder> result = new ArrayList<FileOrFolder>();
		
		for (File fileOrFolder : sortedFiles(folder))
			result.add(readFolderEntry(fileOrFolder));
		
		return result;
	}

	
	private FileOrFolder readFolderEntry(File fileOrFolder) throws IOException {
		Sneer1024 hashOfContents = readIntoTheFileCache(fileOrFolder);
		
		return new FileOrFolder(
			fileOrFolder.getName(),
			fileOrFolder.lastModified(),
			hashOfContents
		);
	}
	
	
	private static ImmutableArray<FileOrFolder> immutable(List<FileOrFolder> entries) {
		return my(ImmutableArrays.class).newImmutableArray(entries);
	}

	
	private static File[] sortedFiles(File folder) {
		File[] result = folder.listFiles();
		if (result == null) return new File[0];
		
		Arrays.sort(result, new Comparator<File>() { @Override public int compare(File file1, File file2) {
			return file1.getName().compareTo(file2.getName());
		}});

		return result;
	}
	
	
	private static byte[] readFileContents(File file) throws IOException {
		return my(IO.class).files().readBytes(file);
	}
	
}
