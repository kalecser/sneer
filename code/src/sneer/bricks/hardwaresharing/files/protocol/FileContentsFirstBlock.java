package sneer.bricks.hardwaresharing.files.protocol;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.foundation.brickness.Seal;

public class FileContentsFirstBlock extends FileContents {

	public final long fileSize; 

	public FileContentsFirstBlock(Seal adressee_, Sneer1024 hashOfFile_, long fileSize_, ImmutableByteArray bytes_, String debugInfo_) {
		super(adressee_, hashOfFile_, 0, bytes_, debugInfo_);
		fileSize = fileSize_;
	}

}
