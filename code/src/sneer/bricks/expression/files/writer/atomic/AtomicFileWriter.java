package sneer.bricks.expression.files.writer.atomic;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.protocol.FolderContents;
import basis.brickness.Brick;

@Brick
public interface AtomicFileWriter {

	/** Writes contents to file using rename for atomicity.
	 * @param lastModified -1 if the lastModified date can be ignored. */
	void writeAtomicallyTo(File file, long lastModified, byte[] contents) throws IOException;

	/** Writes folderContents to new folder using rename for atomicity.
	 * @param lastModified -1 if the lastModified date of folder can be ignored. lastModified dates of the FolderContents will also be ignored if they are set in contents to -1. */
	void writeAtomicallyTo(File folder, long lastModified, FolderContents contents) throws IOException;

}
