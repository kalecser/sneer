package sneer.bricks.expression.files.protocol;

import sneer.bricks.hardware.cpu.codecs.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.Tuple;

public class FileOrFolder extends Tuple {

	public final String name;
	public final long lastModified;
	public final Sneer1024 hashOfContents;
	public final boolean isFolder;

	public FileOrFolder(String name_, long lastModified_, Sneer1024 hashOfContents_, boolean isFolder_) {
		name = name_;
		lastModified = lastModified_;
		hashOfContents = hashOfContents_;
		isFolder = isFolder_;
	}

}
