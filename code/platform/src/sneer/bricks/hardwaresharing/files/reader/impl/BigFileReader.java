package sneer.bricks.hardwaresharing.files.reader.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import sneer.bricks.hardwaresharing.files.cache.FileCache;
import sneer.bricks.hardwaresharing.files.protocol.BigFileBlocks;
import sneer.bricks.hardwaresharing.files.reader.FileReader;
import sneer.bricks.pulp.crypto.Sneer1024;


class BigFileReader {

	static private final FileCache FileCache = my(FileCache.class);
	
	
	static Sneer1024 readFile(File file) throws IOException {
		long length = file.length();
		InputStream stream = new FileInputStream(file);
		try {
			return sliceAndCache(stream, length);
		} finally {
			stream.close();
		}
	}

	
	private static Sneer1024 sliceAndCache(InputStream stream, long length) throws IOException {
		int sliceLength = FileReader.MAXIMUM_FILE_BLOCK_SIZE;
		int slices = countSlices(sliceLength, length);

		Sneer1024[] hashes = new Sneer1024[slices];
		int currentSlice = 0;
		for (int i = 0; i < length; i+=FileReader.MAXIMUM_FILE_BLOCK_SIZE) {
			
			int sliceSize = (int) Math.min(length - i, FileReader.MAXIMUM_FILE_BLOCK_SIZE);
			byte[] buffy = new byte[sliceSize];
			
			int read = stream.read(buffy);
			if (read < sliceSize)
				throw new IOException("Stream has not returned correct number of bytes");
			
			hashes[currentSlice++] = FileCache.putFileContents(buffy);
		}
		
		return packAndCache(hashes);
	}


	private static Sneer1024 packAndCache(Sneer1024[] slices) {
		if (slices.length == 1)
			return slices[0];
		
		int packageSize = countSlices(BigFileBlocks.NUMBER_OF_BLOCKS, slices.length);
		
		Sneer1024[] hashes = new Sneer1024[packageSize];
		int currentBlock = 0;
		for (int i = 0; i < slices.length; i+=BigFileBlocks.NUMBER_OF_BLOCKS) {
			int endOffset = Math.min(BigFileBlocks.NUMBER_OF_BLOCKS, slices.length - i);
			Sneer1024[] copyOfRange = Arrays.copyOfRange(slices, i, i + endOffset);
			BigFileBlocks blocks = new BigFileBlocks(copyOfRange);
			hashes[currentBlock++] = FileCache.putBigFileBlocks(blocks);
		}
		
		return packAndCache(hashes);
	}
	
	
	private static int countSlices(int sliceLength, long totalLength) {
		boolean hasRemainder = (totalLength % sliceLength) != 0l;
		long slices = (totalLength / sliceLength) + (hasRemainder ? 1 : 0);
		
		if (slices != (int)slices)
			throw new IllegalStateException("File is too big");
		
		return (int) slices;
	}
	
}
