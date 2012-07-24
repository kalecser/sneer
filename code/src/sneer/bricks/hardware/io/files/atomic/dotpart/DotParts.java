package sneer.bricks.hardware.io.files.atomic.dotpart;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import basis.brickness.Brick;


@Brick
public interface DotParts {

	File newDotPartFor(File actualFile, String tempName) throws IOException;
	File concludeDotPart(File dotPartFile, File actualFile, long lastModified) throws IOException;

	FileFilter dotPartExclusionFilter();
	String dotPartExtention();

	void deleteAllDotPartsRecursively(File folder) throws IOException;
}
