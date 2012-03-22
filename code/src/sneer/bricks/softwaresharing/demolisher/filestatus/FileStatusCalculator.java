package sneer.bricks.softwaresharing.demolisher.filestatus;

import basis.brickness.Brick;
import sneer.bricks.softwaresharing.FileVersion;

@Brick
public interface FileStatusCalculator {

	FileVersion.Status calculate(byte[] contents, byte[] contentsInCurrentVersion);

}
