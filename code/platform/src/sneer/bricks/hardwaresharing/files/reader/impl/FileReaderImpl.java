package sneer.bricks.hardwaresharing.files.reader.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.cache.FileCache;
import sneer.bricks.hardwaresharing.files.protocol.BigFileBlocks;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.reader.FileReader;
import sneer.bricks.pulp.crypto.Sneer1024;

class FileReaderImpl implements FileReader {

	@Override
	public Sneer1024 readIntoTheFileCache(File fileOrFolder) throws IOException {
		return (fileOrFolder.isDirectory())
			? readFolder(fileOrFolder)
			: readFile(fileOrFolder);
	}


	private static Sneer1024 readFile(File file) throws IOException {
		
		FileCache cache = my(FileCache.class);
		
		long length = file.length();
		boolean isSmallFile = length <= FileReader.MAXIMUM_FILE_BLOCK_SIZE;
		boolean bigFileBlocksDiabled = FileReader.MAXIMUM_FILE_BLOCK_SIZE == -1;
		
		if (bigFileBlocksDiabled ||isSmallFile) {
			return cache.putFileContents(readFileContents(file));
		}
		

		InputStream stream = my(IO.class).files().openAsStream(file);
		try {
			Sneer1024[] slices = sliceAndCache(cache, stream, length);
			return packAndCache(cache, slices);
		} finally {
			stream.close();
		}
		
		
	}


	

	private static Sneer1024 packAndCache(FileCache cache, Sneer1024[] slices) {
	
		if (slices.length == 1)
			return slices[0];
		
		int packageSize = countBoxes(BigFileBlocks.NUMBER_OF_BLOCKS, slices.length);
		
		Sneer1024[] hash = new Sneer1024[packageSize];
		int currentBlock = 0;
		for (int i = 0; i < slices.length; i+=BigFileBlocks.NUMBER_OF_BLOCKS) {
			int endOffset = Math.min(BigFileBlocks.NUMBER_OF_BLOCKS, slices.length - i);
			Sneer1024[] copyOfRange = Arrays.copyOfRange(slices, i, i + endOffset);
			BigFileBlocks blocks = new BigFileBlocks(copyOfRange);
			hash[currentBlock++] = cache.putBigFileBlocks(blocks);
		}
		
		return packAndCache(cache, hash);
	}


	private static Sneer1024[] sliceAndCache(FileCache cache,
			InputStream stream, long length) throws IOException {
		
		int boxSize = FileReader.MAXIMUM_FILE_BLOCK_SIZE;
		int slices = countBoxes(boxSize, length);

		Sneer1024[] hash = new Sneer1024[slices];
		int currentSlice = 0;
		for (int i = 0; i < length; i+=FileReader.MAXIMUM_FILE_BLOCK_SIZE) {
			
			int sliceSize = (int) Math.min(length - i, FileReader.MAXIMUM_FILE_BLOCK_SIZE);
			byte[] buffy = new byte[sliceSize];
			
			int read = stream.read(buffy);
			if (read < sliceSize)
				throw new IOException("Stream has not returned correct number of bytes");
			
			hash[currentSlice++] = cache.putFileContents(buffy);
		}
		
		return hash;
	}


	private static int countBoxes(int boxSize, long ammmountOfObjects) {
		boolean hasReminder = !((ammmountOfObjects % boxSize) == 0l);
		long boxesNeeded = (ammmountOfObjects / boxSize) + (hasReminder?1:0);
		
		if (boxesNeeded != (int)boxesNeeded)
			throw new IllegalStateException("File is too big");
		
		return (int) boxesNeeded;
	}

	
	private Sneer1024 readFolder(File folder) throws IOException {
		return my(FileCache.class).putFolderContents(
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
