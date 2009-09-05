package sneer.bricks.snapps.system.log.file;

import java.io.File;

import sneer.foundation.brickness.Brick;

@Brick
public interface LogToFile {

	void startWritingLogTo(File logFile);
	
}
