package sneer.bricks.softwaresharing.demolisher.filestatus.impl;

import java.util.Arrays;

import sneer.bricks.softwaresharing.FileVersion.Status;
import sneer.bricks.softwaresharing.demolisher.filestatus.FileStatusCalculator;

class FileStatusCalculatorImpl implements FileStatusCalculator {

	@Override
	public Status calculate(byte[] contents, byte[] contentsInCurrentVersion) {
		if (contentsInCurrentVersion == null)
			return Status.EXTRA;
		if (contents == null)
			return Status.MISSING;
		return Arrays.equals(contents, contentsInCurrentVersion) ? Status.CURRENT : Status.DIFFERENT;
	}
}
