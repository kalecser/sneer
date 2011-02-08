package sneer.bricks.softwaresharing.demolisher.filestatus;

import sneer.bricks.softwaresharing.FileVersion;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileStatusCalculator {

	FileVersion.Status calculate(byte[] contents, byte[] contentsInCurrentVersion);

}
