package sneer.bricks.expression.files.protocol;

import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.pulp.keymanager.Seal;

public class FileContentsFirstBlock extends FileContents {

	public final long fileSize; 

	public FileContentsFirstBlock(Seal adressee_, Sneer1024 hashOfFile_, long fileSize_, ImmutableByteArray bytes_, String debugInfo_) {
		super(adressee_, hashOfFile_, 0, bytes_, debugInfo_);
		fileSize = fileSize_;
	}

}
