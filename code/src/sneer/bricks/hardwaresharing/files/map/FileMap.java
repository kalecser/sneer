package sneer.bricks.hardwaresharing.files.map;

import java.io.File;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMap {

	void put(File file, Sneer1024 hash);
	void putFolderContents(File folder, FolderContents contents, Sneer1024 hash);
	void remove(File fileOrFolder);

	File getFile(Sneer1024 hash);
	FolderContents getFolder(Sneer1024 hash);

}
