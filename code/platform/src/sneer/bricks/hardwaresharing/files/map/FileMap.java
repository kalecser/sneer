package sneer.bricks.hardwaresharing.files.map;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardwaresharing.files.protocol.BigFileBlocks;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.events.EventSource;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMap {

	Sneer1024 put(File file) throws IOException;

	Sneer1024 putFileContents(byte[] contents);
	Sneer1024 putBigFileBlocks(BigFileBlocks bigFileBlocks);
	Sneer1024 putFolderContents(FolderContents contents);

	EventSource<Sneer1024> contentsAdded();

	/** @return FolderContents if given hash represents a folder or byte[] if given hash represents a file. */
	Object getContents(Sneer1024 hashOfFileOrFolder);
	boolean isFolder(FileOrFolder fileOrFolder);
}
