package sneer.bricks.hardware.io.file.atomic.dotpart;

import java.io.File;

import sneer.foundation.brickness.Brick;

@Brick
public interface DotParts {

	File openDotPartFor(File actualFile);

	void closeDotPart(File dotPartFile);

}
