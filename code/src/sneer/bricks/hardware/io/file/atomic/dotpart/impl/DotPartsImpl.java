package sneer.bricks.hardware.io.file.atomic.dotpart.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.file.atomic.dotpart.DotParts;

class DotPartsImpl implements DotParts {

	@Override
	public File openDotPartFor(File actualFile) throws IOException {
		return prepareDotPart(actualFile);
	}

	@Override
	public void closeDotPart(File dotPartFile) throws IOException {
		closeDotPart(dotPartFile, -1);
	}

	@Override
	public void closeDotPart(File dotPartFile, long lastModified) throws IOException {
		if (lastModified != -1) dotPartFile.setLastModified(lastModified);
		rename(dotPartFile);
	}

	private File prepareDotPart(File fileOrFolder) throws IOException {
		File result = new File(fileOrFolder.getParent(), fileOrFolder.getName() + ".part");
		my(IO.class).files().forceDelete(result);
		return result;
	}

	private void rename(File dotPartfile) throws IOException {
		final File originalFile = new File(dotPartfile.getPath().replace(".part", "")); 
		if (!dotPartfile.renameTo(originalFile)) throw new IOException("Unable to rename .part file/folder to actual file/folder: " + originalFile);
	}

}
