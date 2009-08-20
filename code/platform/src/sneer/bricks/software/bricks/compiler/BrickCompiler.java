package sneer.bricks.software.bricks.compiler;

import java.io.File;

import sneer.foundation.brickness.Brick;

@Brick
public interface BrickCompiler {

	void compile(File srcFolder, File destinationFolder);

}
