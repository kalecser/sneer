package sneer.bricks.expression.files.protocol;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.foundation.brickness.Seal;
import sneer.foundation.brickness.Tuple;

public class FileContents extends Tuple {

	public final Sneer1024 hashOfFile;
	public final int blockNumber;
	public final ImmutableByteArray bytes;
	public final String debugInfo;

	public FileContents(Seal adressee_, Sneer1024 hashOfFile_, int blockNumber_, ImmutableByteArray bytes_, String debugInfo_) {
		super(adressee_);
		hashOfFile = hashOfFile_;
		blockNumber = blockNumber_;
		bytes = bytes_;
		debugInfo = debugInfo_;
	}

}
