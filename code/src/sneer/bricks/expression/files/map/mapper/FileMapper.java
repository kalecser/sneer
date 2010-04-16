package sneer.bricks.expression.files.map.mapper;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMapper {

	Hash mapFile(File file) throws MappingStopped, IOException;

	Hash mapFolder(File folder, String... acceptedFileExtensions) throws MappingStopped, IOException;
	void stopFolderMapping(File folder);

}
