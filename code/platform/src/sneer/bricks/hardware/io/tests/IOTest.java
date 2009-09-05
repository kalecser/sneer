package sneer.bricks.hardware.io.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.tests.BrickTestWithLogger;

public class IOTest extends BrickTestWithLogger {
	
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
	

}
