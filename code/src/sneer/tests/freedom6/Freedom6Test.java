package sneer.tests.freedom6;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.tests.SovereignFunctionalTestBase;



public class Freedom6Test extends SovereignFunctionalTestBase {

	@Ignore
	@Test (timeout = 3000)
	public void shareAFile() throws IOException {
		File importantFolderA = createFolder("important_folder");
		a().setFolderToBeBackedUp(importantFolderA);
		File file = createTmpFileWithFileNameAsContent("important_folder/important_file.txt");
		
		a().syncBackups(); //Backups the file.
		
		file.delete(); //Oops
		assertFalse(file.exists());
		a().syncBackups(); //Recovers the file.
		assertTrue(file.exists());
		
		String contents = my(IO.class).files().readString(file);
		assertEquals("important_folder/important_file.txt", contents);
	}

	private File createFolder(String fileName) {
		File importantFolderA = newTmpFile(fileName);
		importantFolderA.mkdir();
		return importantFolderA;
	}
	
}