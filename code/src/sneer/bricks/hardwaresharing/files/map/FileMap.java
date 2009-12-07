package sneer.bricks.hardwaresharing.files.map;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMap {
	
	Sneer1024 put(File fileOrFolder, String... acceptedFileExtensions) throws IOException;
	Sneer1024 putFolderContents(FolderContents contents);
	
	File getFile(Sneer1024 hash);
	FolderContents getFolder(Sneer1024 hash);

}
