package sneer.bricks.expression.files.protocol;

import basis.lang.arrays.ImmutableArray;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;

public class FileRequest extends Tuple {

	public final Hash hashOfContents;
	public ImmutableArray<Integer> blockNumbers;
	public final String debugInfo;

	public FileRequest(Seal addressee_, Hash hashOfContents_, ImmutableArray<Integer> blockNumbers_, String debugInfo_) {
		super(addressee_);
		hashOfContents = hashOfContents_;
		blockNumbers = blockNumbers_;
		debugInfo = debugInfo_;
	}

}
