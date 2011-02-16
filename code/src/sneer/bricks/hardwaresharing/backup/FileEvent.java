package sneer.bricks.hardwaresharing.backup;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;

public abstract class FileEvent extends Tuple {

	public final Hash hash;
	public final long lastModified;
	public final String relativePath;

	public FileEvent(Hash hash_, long lastModified_, String relativePath_) {
		hash = hash_;
		lastModified = lastModified_;
		relativePath = relativePath_;
	}

}