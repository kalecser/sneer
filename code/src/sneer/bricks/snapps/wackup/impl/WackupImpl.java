package sneer.bricks.snapps.wackup.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.snapps.wackup.BlockNumberOutOfRange;
import sneer.bricks.snapps.wackup.Wackup;
import sneer.bricks.software.folderconfig.FolderConfig;


class WackupImpl implements Wackup {

	private static final byte[] BLANK_BLOCK = new byte[0];

	private static final int BLOCK_SIZE = 8 * 1024;

	private RandomAccessFile _space;

	private long _sizeInBlocks;

	@Override
	public byte[] read(long number) throws BlockNumberOutOfRange, IOException {
		if (number < 0 || number > _sizeInBlocks)
			throw new BlockNumberOutOfRange("Trying to read block: " + number + "(space size set to " + _sizeInBlocks + " blocks)");

		_space.seek(number * BLOCK_SIZE);
		
		byte[] result = new byte[BLOCK_SIZE];
		_space.readFully(result);
		return result;
	}

	@Override
	public void setSize(long newSize) throws IOException {
		boolean increasing = newSize > _sizeInBlocks;
		_sizeInBlocks = newSize;
		if (_space == null) {
			File tmpFolderFor = my(FolderConfig.class).tmpFolderFor(Wackup.class);
			_space = new RandomAccessFile(new File(tmpFolderFor, "data"), "rw");
		}
		if (increasing)
			write(_sizeInBlocks - 1, BLANK_BLOCK);
	}

	@Override
	public void write(long blockNumber, byte[] block) throws IOException {
		if (block.length > BLOCK_SIZE) throw new IllegalArgumentException();
		
		byte[] blockToWrite = block.length < BLOCK_SIZE
			? Arrays.copyOf(block, BLOCK_SIZE)
			: block;
		
		_space.seek(blockNumber * BLOCK_SIZE);
		_space.write(blockToWrite);
	}

	@Override
	public void crash() {
		if (_space == null) return;
		my(IO.class).crash(_space);
	}

}
