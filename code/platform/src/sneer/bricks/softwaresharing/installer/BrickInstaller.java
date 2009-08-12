package sneer.bricks.softwaresharing.installer;

import java.io.IOException;

import sneer.foundation.brickness.Brick;

@Brick
public interface BrickInstaller {

	void prepareStagedBricksInstallation() throws IOException, BrickCompilationException;

	void commitStagedBricksInstallation();

}
