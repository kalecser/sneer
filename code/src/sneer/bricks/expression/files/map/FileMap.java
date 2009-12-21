package sneer.bricks.expression.files.map;

import java.io.File;

import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMap {

	void putFile(File file, Sneer1024 hash);
	File getFile(Sneer1024 hash);

	void putFolderContents(File folder, FolderContents contents, Sneer1024 hash);
	FolderContents getFolderContents(Sneer1024 hash);

	void remove(File fileOrFolder);

}
