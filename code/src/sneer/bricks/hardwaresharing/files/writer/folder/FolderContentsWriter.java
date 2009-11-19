package sneer.bricks.hardwaresharing.files.writer.folder;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.foundation.brickness.Brick;

@Brick
public interface FolderContentsWriter {

	void writeToFolder(File folder, FolderContents contents) throws IOException;

	void mergeOver(File existingFolder, FolderContents folderContents) throws IOException;

}
