package sneer.bricks.hardware.io.files.atomic.dotpart.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.hardware.io.log.Logger;

class DotPartsImpl implements DotParts {

	private static final String DOT_PART_EXTENSION = ".part";

	@Override
	public File openDotPartFor(File actualFile) throws IOException {
		if (actualFile.exists()) throw new IOException("File already exists: " + actualFile);
		
		File result = new File(actualFile.getParent(), actualFile.getName() + DOT_PART_EXTENSION);
		my(IO.class).files().forceDelete(result);
		return result;
	}


	@Override
	public File closeDotPart(File dotPartFile, long lastModified) throws IOException {
		my(Logger.class).log("Closing dotPart file: ", dotPartFile);
		
		if (lastModified != -1)
			dotPartFile.setLastModified(lastModified);
		
		final File actualFile = actualFile(dotPartFile);
		if (!dotPartFile.renameTo(actualFile))
			throw new IOException(unableToRenameMessage(dotPartFile, actualFile));

		return actualFile;
	}


	private String unableToRenameMessage(File dotPartFile, File actualFile) {
		String result = "Unable to rename .part file/folder to actual file/folder: " + actualFile;
		if (actualFile.exists()  ) result += " (actual file/folder already exists)";
		if (!dotPartFile.exists()) result += " (.part file/folder does not exist)";
		
		return result;
	}


	private File actualFile(File dotPartFile) {
		return new File(my(Lang.class).strings().chomp(dotPartFile.getAbsolutePath(), DOT_PART_EXTENSION));
	}


	@Override
	public FileFilter dotPartExclusionFilter() {
		return my(IO.class).fileFilters().not(
			my(IO.class).fileFilters().suffix(DOT_PART_EXTENSION)
		);
	}


	@Override
	public String dotPartExtention() {
		return DOT_PART_EXTENSION;
	}

}
