package sneer.bricks.hardware.io.files.atomic.dotpart.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.IO.Filter;
import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.hardware.io.log.Logger;

class DotPartsImpl implements DotParts {

	private static final String DOT_PART_PURE_EXTENSION = "part-file";
	private static final String DOT_PART_EXTENSION = "." + DOT_PART_PURE_EXTENSION;

	@Override
	public File openDotPartFor(File actualFile, String tempName) throws IOException {
		if (actualFile.exists()) throw new IOException("File already exists: " + actualFile);
		
		return new File(actualFile.getParent(), tempName + DOT_PART_EXTENSION);
	}



	@Override
	public File closeDotPart(File dotPartFile, File actualFile, long lastModified) throws IOException {
		my(Logger.class).log("Closing dotPart file: ", dotPartFile);
		
		if (lastModified != -1)
			dotPartFile.setLastModified(lastModified);
		
		if (!dotPartFile.renameTo(actualFile))
			throw new IOException(unableToRenameMessage(dotPartFile, actualFile));

		return actualFile;
	}


	private String unableToRenameMessage(File dotPartFile, File actualFile) {
		String result = "Unable to rename .part-file file/folder to actual file/folder: " + actualFile;
		if (actualFile.exists()  ) result += " (actual file/folder already exists)";
		if (!dotPartFile.exists()) result += " (.part file/folder does not exist)";
		
		return result;
	}


	@Override
	public FileFilter dotPartExclusionFilter() {
		return my(IO.class).fileFilters().not(
			dotPartFilter()
		);
	}


	private Filter dotPartFilter() {
		return my(IO.class).fileFilters().suffix(DOT_PART_EXTENSION);
	}


	@Override
	public String dotPartExtention() {
		return DOT_PART_EXTENSION;
	}


	@Override
	public void deleteAllDotPartsRecursively(File folder) throws IOException {
		for (File file : dotPartFilesRecursivelyIn(folder))
			my(IO.class).files().forceDelete(file);
	}


	private Collection<File> dotPartFilesRecursivelyIn(File folder) {
		return my(IO.class).files().listFiles(folder, new String[]{DOT_PART_PURE_EXTENSION}, true);
	}

}

