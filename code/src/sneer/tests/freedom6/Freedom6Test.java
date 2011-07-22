package sneer.tests.freedom6;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.tests.SovereignFunctionalTestBase;


public class Freedom6Test extends SovereignFunctionalTestBase {

	@Test (timeout = 20000)
	public void syncOneFile() throws IOException {
		syncFiles("important_file.txt");
	}

	
	@Ignore
	@Test (timeout = 20000)
	public void syncSeveralFiles() throws IOException {
		syncFiles("file1.txt", "file2.mp3");
	}
	
	
	private void syncFiles(String... files) throws IOException {
		File folder = createFolder("important_folder");
		for (String file : files)
			createTmpFileWithFileNameAsContent("important_folder\\" + file);
		
		a().setFolderToSync(folder);
		b().lendSpaceTo(a().ownName(), 10);
		a().waitForSync();

		File newFolder = createFolder("new_folder");
		a().setFolderToSync(newFolder);
		a().waitForSync();
		
		for (String file : files) {
			File recoveredFile = new File(newFolder, file);
			assertEquals("important_folder\\" + file, contents(recoveredFile));
		}
	}

	
	private String contents(File recoveredFile) throws IOException {
		return my(IO.class).files().readString(recoveredFile);
	}

	
	private File createFolder(String fileName) {
		File importantFolderA = newTmpFile(fileName);
		importantFolderA.mkdir();
		return importantFolderA;
	}
	
}