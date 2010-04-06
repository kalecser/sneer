package sneer.bricks.expression.files.protocol;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.pulp.tuples.Tuple;

public class FileOrFolder extends Tuple {

	public final String name;
	public final long lastModified;
	public final Hash hashOfContents;
	public final boolean isFolder;

	public FileOrFolder(String name_, long lastModified_, Hash hashOfContents_, boolean isFolder_) {
		name = name_;
		lastModified = lastModified_;
		hashOfContents = hashOfContents_;
		isFolder = isFolder_;
	}

}
