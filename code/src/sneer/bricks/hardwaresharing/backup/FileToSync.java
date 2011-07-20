package sneer.bricks.hardwaresharing.backup;

import sneer.bricks.hardware.cpu.crypto.Hash;

public class FileToSync extends FileEvent {

	public FileToSync(Hash hash_, long lastModified_, String relativePath_) {
		super(hash_, lastModified_, relativePath_);
	}

}
