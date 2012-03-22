package sneer.bricks.softwaresharing.demolisher.filestatus.tests;

import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.bricks.softwaresharing.FileVersion;
import sneer.bricks.softwaresharing.FileVersion.Status;
import sneer.bricks.softwaresharing.demolisher.filestatus.FileStatusCalculator;
import static basis.environments.Environments.my;

public class FileStatusCalculatorTest extends BrickTestBase {
	
	@Test
	public void extra() {
		assertEquals(FileVersion.Status.EXTRA, calculate(new byte[0], null));
		assertEquals(FileVersion.Status.MISSING, calculate(null, new byte[0]));
		assertEquals(FileVersion.Status.CURRENT, calculate(new byte[0], new byte[0]));
		assertEquals(FileVersion.Status.DIFFERENT, calculate(new byte[0], new byte[1]));
	}

	private Status calculate(byte[] contents, byte[] contentsInCurrentVersion) {
		return my(FileStatusCalculator.class).calculate(contents, contentsInCurrentVersion);
	}
}
