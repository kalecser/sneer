package sneer.bricks.expression.files.map;

import java.io.File;

import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.Brick;

@Brick (Prevalent.class)
public interface FileMap {

	void putFile(File file, Sneer1024 hash);
	void putFile(File file, long lastModified, Sneer1024 hash);
	File getFile(Sneer1024 hash);
	Sneer1024 getHash(File file);
	long getLastModified(File file);

	void putFolderContents(File folder, FolderContents contents, Sneer1024 hash);
	FolderContents getFolderContents(Sneer1024 hash);

	Sneer1024 remove(File fileOrFolder);
	void rename(File fileOrFolder, File newFileOrFolder); 

}
