package sneer.bricks.softwaresharing.installer;

import java.io.IOException;

import sneer.bricks.software.code.compilers.java.JavaCompilerException;
import sneer.foundation.brickness.Brick;

@Brick
public interface BrickInstaller {

	void prepareStagedBricksInstallation() throws IOException, JavaCompilerException;

	void commitStagedBricksInstallation();

}
