package sneer.bricks.hardwaresharing.files.protocol;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Tuple;

public class FileRequest extends Tuple {

	public final Sneer1024 hashOfContents;
	public final String debugInfo;

	public FileRequest(Sneer1024 hashOfContents_, String debugInfo_) {
		hashOfContents = hashOfContents_;
		debugInfo = debugInfo_;
	}
	

}
