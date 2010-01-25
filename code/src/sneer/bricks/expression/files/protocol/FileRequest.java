package sneer.bricks.expression.files.protocol;

import sneer.bricks.hardware.cpu.codecs.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.Tuple;

public class FileRequest extends Tuple {

	public final Sneer1024 hashOfContents;
	public final int blockNumber;
	public final String debugInfo;

	public FileRequest(Sneer1024 hashOfContents_, int blockNumber_, String debugInfo_) {
		hashOfContents = hashOfContents_;
		blockNumber = blockNumber_;
		debugInfo = debugInfo_;
	}

}
