package sneer.bricks.expression.files.protocol;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;

public class FileRequest extends Tuple {

	public final Hash hashOfContents;
	public final int blockNumber;
	public final String debugInfo;

	public FileRequest(Seal addressee_, Hash hashOfContents_, int blockNumber_, String debugInfo_) {
		super(addressee_);
		hashOfContents = hashOfContents_;
		blockNumber = blockNumber_;
		debugInfo = debugInfo_;
	}

}
