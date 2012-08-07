package sneer.bricks.expression.files.protocol;

import basis.lang.Immutable;
import sneer.bricks.hardware.cpu.crypto.Hash;

public class FileOrFolder extends Immutable {

	public final String name;
	public final long size;
	public final long lastModified;
	public final Hash hashOfContents;
	public final boolean isFolder;

	public FileOrFolder(String name_, long size_, long lastModified_, Hash hashOfContents_) {
		name = name_;
		size = size_;
		lastModified = lastModified_;
		hashOfContents = hashOfContents_;
		isFolder = false;
	}

	public FileOrFolder(String name_, Hash hashOfContents_) {
		name = name_;
		size = -1;
		lastModified = -1;
		hashOfContents = hashOfContents_;
		isFolder = true;
	}

}
