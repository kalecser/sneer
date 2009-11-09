package sneer.bricks.hardware.io.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class IOTest extends BrickTest {
	
	private IO _subject = my(IO.class);

	@Test
	public void folderCopySpike() throws Exception  {
		File file = new File(tmpFolder(), "a1/a2/a3");
		file.getParentFile().mkdirs();
		file.createNewFile();
		Thread.sleep(100); //Make sure copy does not happen in the same millisecond. 
		_subject.files().copyFolder(new File(tmpFolder(), "a1"), new File(tmpFolder(), "b1"));
		
		_subject.files().assertSameContents(new File(tmpFolder(), "a1"), new File(tmpFolder(), "b1"));
	}

	@Test
	public void readBlockTest() throws IOException {
		File tmpFile = createTmpFileWithFileNameAsContent("A234567890B234567890C234567890D2");
		assertEquals("A234567890", new String(_subject.files().readBlock(tmpFile, 0, 10)));
		assertEquals("B234567890", new String(_subject.files().readBlock(tmpFile, 1, 10)));
		assertEquals("C234567890", new String(_subject.files().readBlock(tmpFile, 2, 10)));
		assertEquals("D2", new String(_subject.files().readBlock(tmpFile, 3, 10)));
	}

}
