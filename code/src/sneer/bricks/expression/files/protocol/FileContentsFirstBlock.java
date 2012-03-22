package sneer.bricks.expression.files.protocol;

import basis.lang.arrays.ImmutableByteArray;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;

public class FileContentsFirstBlock extends FileContents {

	public final long fileSize; 

	public FileContentsFirstBlock(Seal adressee_, Hash hashOfFile_, long fileSize_, ImmutableByteArray bytes_, String debugInfo_) {
		super(adressee_, hashOfFile_, 0, bytes_, debugInfo_);
		fileSize = fileSize_;
	}

}
