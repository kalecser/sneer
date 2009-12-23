package sneer.bricks.expression.files.map.mapper;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMapper {

	Sneer1024 mapFile(File file) throws MappingStopped, IOException;

	Sneer1024 mapFolder(File folder, String... acceptedFileExtensions) throws MappingStopped;

	void stopFolderMapping(File folder);

}
