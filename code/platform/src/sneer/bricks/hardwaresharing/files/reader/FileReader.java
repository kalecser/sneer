package sneer.bricks.hardwaresharing.files.reader;

import java.io.File;
import java.io.IOException;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileReader {

	/** Reads the contents of fileOrFolder into the FileCache.
	 * @return handle to be used to retrieve the read contents of fileOrFolder from the cache. */
	Sneer1024 readIntoTheFileCache(File fileOrFolder) throws IOException;

}
