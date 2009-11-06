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
		File tmpFile = createTmpFileWithFileNameAsContent("/1stBlockContent/2ndBlockContent/3rdBlockContent");
		assertEquals("/1stBlockContent", new String(_subject.files().readBlock(tmpFile, 0, 16)));
		assertEquals("/2ndBlockContent", new String(_subject.files().readBlock(tmpFile, 1, 16)));
		assertEquals("/3rdBlockContent", new String(_subject.files().readBlock(tmpFile, 2, 16)));
	}

}
