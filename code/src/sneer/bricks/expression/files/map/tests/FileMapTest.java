package sneer.bricks.expression.files.map.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class FileMapTest extends BrickTest {

	private final FileMap _subject = my(FileMap.class);

	@Test
	public void fileMapping() {
		File file = anySmallFile();
		Sneer1024 hash = hash(42); 
		_subject.putFile(file, hash);
		assertEquals(file,_subject.getFile(hash));

		_subject.remove(file);
		assertNull(_subject.getFile(hash));
	}

	@Test
	public void rename() {
		_subject.putFile(new File("folder/sub/file1.txt"),	41, hash(1));
		_subject.putFile(new File("folder/sub/file2.txt"),	42, hash(2));
		_subject.putFile(new File("folder/file3.txt"),		43, hash(3));
		_subject.putFile(new File("folder/file4.txt"),		44, hash(4));
		_subject.putFile(new File("folder2/file5.txt"),		45, hash(5));

		_subject.rename(new File("folder"), new File("newFolder"));

		assertFileWasRenamed("newFolder/sub/file1.txt",	41, hash(1));
		assertFileWasRenamed("newFolder/sub/file2.txt",	42, hash(2));
		assertFileWasRenamed("newFolder/file3.txt",		43, hash(3));
		assertFileWasRenamed("newFolder/file4.txt",		44, hash(4));

		assertNull(_subject.getHash(new File("newFolder/file5.txt")));

		_subject.rename(new File("newFolder/sub"), new File("newFolder/newSub"));

		assertFileWasRenamed("newFolder/newSub/file1.txt", 41, hash(1));
		assertFileWasRenamed("newFolder/newSub/file2.txt", 42, hash(2));
	}

	private void assertFileWasRenamed(String fileName, int lastModified, Sneer1024 hash) {
		File file = new File(fileName);
		assertEquals(hash, _subject.getHash(file));
		assertEquals(lastModified, _subject.getLastModified(file));
	}

	private File anySmallFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

	private Sneer1024 hash(int b) {
		return my(Crypto.class).digest(new byte[] { (byte) b });
	}

}
