package sneer.bricks.snapps.wackup;

import java.io.IOException;

import sneer.foundation.brickness.Brick;

@Brick
public interface Wackup {

	byte[] read(long blockNumber) throws BlockNumberOutOfRange, IOException;

	void setSize(long sizeInBlocks) throws IOException;

	void write(long blockNumber, byte[] block) throws IOException, BlockNumberOutOfRange;

	void crash();

}
