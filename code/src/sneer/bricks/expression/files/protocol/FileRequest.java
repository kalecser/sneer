package sneer.bricks.expression.files.protocol;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;

public class FileRequest extends Tuple {

	public final Hash hashOfContents;
	public final int blockNumber;
	public final String debugInfo;

	public FileRequest(Hash hashOfContents_, int blockNumber_, String debugInfo_) {
		hashOfContents = hashOfContents_;
		blockNumber = blockNumber_;
		debugInfo = debugInfo_;
	}

}
