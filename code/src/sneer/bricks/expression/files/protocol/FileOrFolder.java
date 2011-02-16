package sneer.bricks.expression.files.protocol;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.foundation.lang.Immutable;

public class FileOrFolder extends Immutable {

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
