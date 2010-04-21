package sneer.bricks.expression.files.protocol;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.lang.arrays.ImmutableByteArray;

public class FileContents extends Tuple {

	public final Hash hashOfFile;
	public final int blockNumber;
	public final ImmutableByteArray bytes;
	public final String debugInfo;

	public FileContents(Seal adressee_, Hash hashOfFile_, int blockNumber_, ImmutableByteArray bytes_, String debugInfo_) {
		super(adressee_);
		hashOfFile = hashOfFile_;
		blockNumber = blockNumber_;
		bytes = bytes_;
		debugInfo = debugInfo_;
	}

}
