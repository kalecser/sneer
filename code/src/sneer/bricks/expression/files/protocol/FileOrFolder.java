package sneer.bricks.expression.files.protocol;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;

public class FileOrFolder extends Tuple {

	public final String name;
	public final long lastModified;
	public final Hash hashOfContents;
	public final boolean isFolder;

	public FileOrFolder(String name_, long lastModified_, Hash hashOfContents_) {
		name = name_;
		lastModified = lastModified_;
		hashOfContents = hashOfContents_;
		isFolder = false;
	}

	public FileOrFolder(String name_, Hash hashOfContents_) {
		name = name_;
		lastModified = -1;
		hashOfContents = hashOfContents_;
		isFolder = true;
	}

}
