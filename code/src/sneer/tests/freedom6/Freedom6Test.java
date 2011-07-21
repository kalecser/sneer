package sneer.tests.freedom6;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.tests.SovereignFunctionalTestBase;


@Ignore
public class Freedom6Test extends SovereignFunctionalTestBase {

	@Test (timeout = 20000)
	public void syncAFile() throws IOException {
		
		File folder = createFolder("important_folder");
		createTmpFileWithFileNameAsContent("important_folder/important_file.txt");
		
		a().setFolderToSync(folder);
		b().lendSpaceTo(a().ownName(), 10);
		a().waitForSync();

		File newFolder = createFolder("new_folder");
		a().setFolderToSync(newFolder);
		a().waitForSync();
		
		File recoveredFile = new File(newFolder, "important_file.txt");
		assertEquals("important_folder/important_file.txt", contents(recoveredFile));
	}

	@Test (timeout = 20000)
	public void syncSeveralFiles() throws IOException {
		
		File folder = createFolder("important_folder");
		createTmpFileWithFileNameAsContent("important_folder/important_file_one.txt");
		createTmpFileWithFileNameAsContent("important_folder/important_file_two.txt");
		//createTmpFileWithFileNameAsContent("important_folder/important_file_three.txt");
		
		a().setFolderToSync(folder);
		b().lendSpaceTo(a().ownName(), 10);
		a().waitForSync();

		File newFolder = createFolder("new_folder");
		a().setFolderToSync(newFolder);
		a().waitForSync();
		
		File recoveredFileOne = new File(newFolder, "important_file_one.txt");
		File recoveredFileTwo = new File(newFolder, "important_file_two.txt");
		//File recoveredFileThree = new File(newFolder, "important_file_three.txt");
		assertEquals("important_folder/important_file_one.txt", contents(recoveredFileOne));
		assertEquals("important_folder/important_file_two.txt", contents(recoveredFileTwo));
		//assertEquals("important_folder/important_file_three.txt", contents(recoveredFileThree));
		
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