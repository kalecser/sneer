package sneer.bricks.expression.files.map.mapper;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMapper {

	Sneer1024 map(File fileOrFolder, String... acceptedFileExtensions) throws MappingStopped, IOException;

	void stopFolderMapping(File folder);

}
