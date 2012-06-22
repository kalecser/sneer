package sneer.bricks.expression.files.map.mapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import basis.brickness.Brick;

import sneer.bricks.hardware.cpu.crypto.Hash;

@Brick
public interface FileMapper {

	/** Starts updating the FileMap with the current state of fileOrFolder.
	 * @param acceptedFileExtensions Ignored if fileOrFolder is a file. */
	Hash mapFileOrFolder(File fileOrFolder, String... acceptedFileExtensions) throws MappingStopped, IOException;
	
	/** Stops the updating thread of fileOrFolder. */
	void stopMapping(File fileOrFolder);

	/** Several files might be mapped with the same hash, returns the first one that still exists (has not been renamed or deleted on the file system). */ 
	File getExistingMappedFile(Hash hash) throws FileNotFoundException;

}
