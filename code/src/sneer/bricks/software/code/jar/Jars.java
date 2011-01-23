package sneer.bricks.software.code.jar;

import java.io.File;
import java.io.IOException;

import sneer.foundation.brickness.Brick;

@Brick
public interface Jars {

	JarBuilder builder(File jarFile) throws IOException;

	void build(File jarFile, File binFolder) throws IOException;
}
