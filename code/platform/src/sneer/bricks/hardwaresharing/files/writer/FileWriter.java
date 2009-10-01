package sneer.bricks.hardwaresharing.files.writer;

import java.io.File;
import java.io.IOException;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileWriter {

	/** Retrieves the contents from the FileMap given hashOfContents and writes them to fileOrFolder using folder rename for atomicity. */
	void writeAtomicallyTo(File fileOrFolder, long lastModified, Sneer1024 hashOfContents) throws IOException;

	void mergeOver(File existingFolder, Sneer1024 hashOfContents);

}
