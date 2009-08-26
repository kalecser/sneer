package sneer.bricks.software.code.java.source.writer;

import java.io.File;

import sneer.foundation.brickness.Brick;

@Brick
public interface JavaSourceWriters {

	JavaSourceWriter newInstance(File srcFolder);
	
}
