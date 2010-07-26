package sneer.bricks.expression.files.map.mapper;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMapper {

	/** Updates the FileMap with the current state of fileOrFolder.
	 * @param acceptedFileExtensions Ignored if fileOrFolder is a file. */
	Hash mapFileOrFolder(File fileOrFolder, String... acceptedFileExtensions) throws MappingStopped, IOException;
	void stopMapping(File fileOrFolder);

}
