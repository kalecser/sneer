package sneer.tests.freedom6;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.tests.SovereignFunctionalTestBase;



public class Freedom6Test extends SovereignFunctionalTestBase {

	//@Ignore
	@Test (timeout = 6000)
	public void shareSpaceForBackupOfOneFile() throws IOException {
		File folder1 = createFolder("folder1");
		a().setFolderToBeBackedUp(folder1);
		File file = createTmpFileWithFileNameAsContent("folder1/important_file.txt");
		
		b().lendBackupSpaceTo(a().ownName(), 10);
		a().waitForBackupSync(); //Backups the file.
		
		File folder2 = createFolder("folder2");
		File recoveredFile = newTmpFile("folder2/important_file.txt");
		assertFalse(recoveredFile.exists());
		a().setFolderToBeBackedUp(folder2);
		a().waitForBackupSync();

		assertTrue(recoveredFile.exists());
		
		String contents = my(IO.class).files().readString(file);
		assertEquals("folder1/important_file.txt", contents);
	}

	private File createFolder(String fileName) {
		File result = newTmpFile(fileName);
		assertTrue(result.mkdir());
		return result;
	}
	
}