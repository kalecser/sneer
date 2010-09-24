package sneer.bricks.hardwaresharing.dataspace;

import java.io.IOException;

public interface Bucket {

	byte[] read(long blockNumber) throws BlockNumberOutOfRange, IOException;

	void setSize(long sizeInBlocks) throws IOException;

	void write(long blockNumber, byte[] block) throws IOException, BlockNumberOutOfRange;

	void crash();

}
