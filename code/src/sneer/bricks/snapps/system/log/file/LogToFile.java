package sneer.bricks.snapps.system.log.file;

import java.io.File;

import basis.brickness.Brick;


@Brick
public interface LogToFile {

	void startWritingLogTo(File logFile);
	
}
