package sneer.bricks.hardwaresharing.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.hardwaresharing.files.reader.FileReader;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.software.code.classutils.ClassUtils;

/** Abstract test class names must not end in "Test" or else Hudson will try to instantiate them and fail. :P */
public abstract class FileCopyTestBase extends TestThatUsesLogger {

	private final FileReader _publisher = my(FileReader.class);

	
	@Test (timeout = 3000)
	public void testWithSmallFile() throws IOException {
		testWith(anySmallFile());
	}

	
	@Test (timeout = 3000)
	public void testWithAFewFiles() throws IOException {
		testWith(folderWithAFewFiles());
	}


	private void testWith(File fileOrFolder) throws IOException {
		Sneer1024 hash = _publisher.readIntoTheFileCache(fileOrFolder);
		assertNotNull(hash);

		File copy = newTempFile(); 
		copyFromFileCache(hash, copy);
		
		assertSameContents(fileOrFolder, copy);
	}


	abstract protected void copyFromFileCache(Sneer1024 hashOfContents, File destination) throws IOException;


	private File anySmallFile() {
		return myClassFile();
	}

	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

	private File folderWithAFewFiles() {
		return new File(myClassFile().getParent(), "fixtures");
	}

	private void assertSameContents(File file1, File file2) throws IOException {
		my(IO.class).files().assertSameContents(file1, file2);
	}

	private File newTempFile() {
		return new File(tmpFolder(), "copy" + System.nanoTime());
	}

}