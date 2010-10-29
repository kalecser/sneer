package sneer.bricks.hardwaresharing.dataspace.buckets.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.dataspace.buckets.BlockNumberOutOfRange;
import sneer.bricks.hardwaresharing.dataspace.buckets.Bucket;
import sneer.bricks.software.folderconfig.FolderConfig;


class BucketImpl implements Bucket {

	private static final byte[] EMPTY_BLOCK = new byte[0];
	private static final int BLOCK_SIZE = 8 * 1024;

	private long _sizeInBlocks;
	private RandomAccessFile _file;

	
	@Override
	public byte[] read(long number) throws BlockNumberOutOfRange, IOException {
		checkRange(number);

		_file.seek(number * BLOCK_SIZE);
		
		byte[] result = new byte[BLOCK_SIZE];
		_file.readFully(result);
		return result;
	}


	private void checkRange(long number) throws BlockNumberOutOfRange {
		if (number < 0 || number > _sizeInBlocks)
			throw new BlockNumberOutOfRange("Trying to read block: " + number + "(space size set to " + _sizeInBlocks + " blocks)");
	}

	
	@Override
	public void setSize(long newSize) throws IOException {
		boolean increasing = newSize > _sizeInBlocks;
		_sizeInBlocks = newSize;
		if (_file == null)
			initSpace();

		if (increasing)
			allocateSpace();
	}


	private void allocateSpace() throws IOException {
		try {
			write(_sizeInBlocks - 1, EMPTY_BLOCK);
		} catch (BlockNumberOutOfRange e) {
			throw new IllegalStateException(e);
		}
	}


	private void initSpace() throws FileNotFoundException {
		File tmpFolderFor = my(FolderConfig.class).tmpFolderFor(Bucket.class);
		_file = new RandomAccessFile(new File(tmpFolderFor, "data"), "rw");
	}

	@Override
	public void write(long blockNumber, byte[] block) throws IOException, BlockNumberOutOfRange {
		checkRange(blockNumber);
		if (block.length > BLOCK_SIZE) throw new IllegalArgumentException();
		
		byte[] blockToWrite = block.length < BLOCK_SIZE
			? Arrays.copyOf(block, BLOCK_SIZE)
			: block;
		
		_file.seek(blockNumber * BLOCK_SIZE);
		_file.write(blockToWrite);
	}

	@Override
	public void crash() {
		my(IO.class).crash(_file);
	}

}
