package sneer.bricks.hardwaresharing.backup;

import sneer.bricks.hardware.cpu.crypto.Hash;

public class FileToSync extends FileEvent {

	public FileToSync(Hash hash_, long size, long lastModified_, String relativePath_) {
		super(hash_, size, lastModified_, relativePath_);
	}

}
