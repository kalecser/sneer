package sneer.bricks.software.bricks.compiler;

import java.io.File;
import java.io.IOException;

import basis.brickness.Brick;


@Brick
public interface Builder {

	void build(File srcFolder, File binFolder) throws BrickCompilerException, IOException;

}
