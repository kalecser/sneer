package sneer.bricks.pulp.serialization;

import static sneer.foundation.environments.Environments.my;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.foundation.brickness.Tuple;

public class LargeFileContentsHack extends Tuple {

	private static final int BLOCK_SIZE = 10000;
	
	private final List<byte[]> _blocks = new ArrayList<byte[]>();


	
	public LargeFileContentsHack(FileContents original) {
		super(original.addressee);
		stamp(original.publisher(), original.publicationTime());
		readBlocks(original);
	}


	private void readBlocks(FileContents original) {
		ByteArrayInputStream input = new ByteArrayInputStream(original.bytes.copy());

		while (true) {
			byte[] block = new byte[BLOCK_SIZE];
			int count = read(input, block);
			if (count == -1) return;
			if (count != BLOCK_SIZE)
				block = truncate(block, count);
			_blocks.add(block);
		}
	}

	
	public Object unmarshall() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		for (byte[] block : _blocks)
			write(output, block);
		
		FileContents result = new FileContents(addressee, my(ImmutableArrays.class).newImmutableByteArray(output.toByteArray()));
		result.stamp(publisher(), publicationTime());
		return result;
	}


	private void write(ByteArrayOutputStream output, byte[] block) {
		try {
			output.write(block);
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	
	private byte[] truncate(byte[] array, int elements) {
		byte[] result = new byte[elements];
		System.arraycopy(array, 0, result, 0, elements);
		return result;
	}


	private int read(ByteArrayInputStream input, byte[] buffer) {
		try {
			return input.read(buffer);
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}



}
