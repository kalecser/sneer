package sneer.bricks.expression.files.map;

import java.io.File;

import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.Brick;

@Brick (Prevalent.class)
public interface FileMap {

	void putFile(File file, long lastModified, Hash hash);
	File getFile(Hash hash);
	Hash getHash(File file);
	long getLastModified(File file);

	void putFolderContents(File folder, FolderContents contents, Hash hash);
	FolderContents getFolderContents(Hash hash);

	Hash remove(File fileOrFolder);
	void rename(File fileOrFolder, File newFileOrFolder); 

}
