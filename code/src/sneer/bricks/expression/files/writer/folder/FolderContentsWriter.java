package sneer.bricks.expression.files.writer.folder;

import java.io.File;
import java.io.IOException;

import basis.brickness.Brick;

import sneer.bricks.expression.files.protocol.FolderContents;

@Brick
public interface FolderContentsWriter {

	void writeToFolder(File folder, FolderContents contents) throws IOException;

	void mergeOver(File existingFolder, FolderContents folderContents) throws IOException;

}
