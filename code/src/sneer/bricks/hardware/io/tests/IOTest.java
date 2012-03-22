package sneer.bricks.hardware.io.tests;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import basis.lang.Functor;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class IOTest extends BrickTestBase {
	
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

	
	@Test
	public void fileExtensionFilterTest() throws IOException {
		createTmpSubfolder();
		createTmpFiles("file1.txt", "file2.rtf", "file3.doc", "file4.htm", "file5.txt");

		assertContents(listFileNames(tmpFolder()), "subdirectory");
		assertContentsInAnyOrder(listFileNames(tmpFolder(), "rtf"), "subdirectory", "file2.rtf");
		assertContentsInAnyOrder(listFileNames(tmpFolder(), "doc"), "subdirectory", "file3.doc");
		assertContentsInAnyOrder(listFileNames(tmpFolder(), "htm"), "subdirectory", "file4.htm");
		assertContentsInAnyOrder(listFileNames(tmpFolder(), "txt"), "subdirectory", "file1.txt", "file5.txt");
	}

	private void createTmpSubfolder() {
		new File(tmpFolder(), "subdirectory").mkdir();
	}

	private Collection<String> listFileNames(File folder, String... fileExtensions) {
		return my(CollectionUtils.class).map(listFiles(folder, fileExtensions), new Functor<File, String> () { @Override public String evaluate(File file) throws RuntimeException {
			return file.getName();
		}});
	}

	private List<File> listFiles(File folder, String... fileExtensions) {
		return Arrays.asList(folder.listFiles(_subject.fileFilters().foldersAndExtensions(fileExtensions)));
	}

}
