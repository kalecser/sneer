package sneer.bricks.hardware.io.files.atomic.dotpart.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;

class DotPartsImpl implements DotParts {

	@Override
	public File openDotPartFor(File actualFile) throws IOException {
		if (actualFile.exists()) throw new IOException("File already exists: " + actualFile);
		
		File result = new File(actualFile.getParent(), actualFile.getName() + ".part");
		my(IO.class).files().forceDelete(result);
		return result;
	}


	@Override
	public void closeDotPart(File dotPartFile, long lastModified) throws IOException {
		if (lastModified != -1)
			dotPartFile.setLastModified(lastModified);
		
		if (!dotPartFile.renameTo(actualFile(dotPartFile)))
			throw new IOException("Unable to rename .part file/folder to actual file/folder: " + actualFile(dotPartFile));
	}


	private File actualFile(File dotPartFile) {
		return new File(my(Lang.class).strings().chomp(dotPartFile.getAbsolutePath(), ".part"));
	}

}
