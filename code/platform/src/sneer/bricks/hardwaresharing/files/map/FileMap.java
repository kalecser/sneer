package sneer.bricks.hardwaresharing.files.map;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.events.EventSource;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMap {

	static final int FILE_BLOCK_SIZE = Integer.MAX_VALUE;
	Sneer1024 put(File file) throws IOException;
	Sneer1024 putFolderContents(FolderContents contents);
	File getFile(Sneer1024 hash);
	FolderContents getFolder(Sneer1024 hash);

	
	EventSource<Sneer1024> contentsAdded();

	/** @return FolderContents if given hash represents a folder or byte[] if given hash represents a file. */
	Object getContents(Sneer1024 hashOfFileOrFolder);
	boolean isFolder(FileOrFolder fileOrFolder);


}
