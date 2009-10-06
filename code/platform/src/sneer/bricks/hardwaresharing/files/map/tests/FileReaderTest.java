package sneer.bricks.hardwaresharing.files.map.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class FileReaderTest extends BrickTest {

	private final FileMap _subject = my(FileMap.class);

	
	@Test (timeout = 3000)
	public void readSmallFileToTheCache() throws IOException {
		File file = fixture("file1.txt");
		Sneer1024 hash = _subject.put(file);
		assertEquals(file,_subject.getFile(hash));
	}

	@Test (timeout = 3000)
	public void readFolderToTheCache() {
		
/*		Sneer1024 hash = _subject.put(fixturesFolder());
		FolderContents folderContents = _subject.getFolder(hash);
		folderContents.contents.
		
		checking(new Expectations(){{
			Sequence seq = newSequence("whatever");
			exactly(1).of(_cache).putFileContents(contents("directory1/file1.txt")); inSequence(seq);
			exactly(1).of(_cache).putFileContents(contents("directory1/file3.txt")); inSequence(seq);
			exactly(1).of(_cache).putFolderContents(with(any(FolderContents.class))); inSequence(seq);
			exactly(1).of(_cache).putFileContents(contents("directory2/users.png")); inSequence(seq);
			exactly(1).of(_cache).putFolderContents(with(any(FolderContents.class))); inSequence(seq);
			exactly(1).of(_cache).putFileContents(contents("file1.txt")); inSequence(seq);
			exactly(1).of(_cache).putFileContents(contents("file2.txt")); inSequence(seq);
			exactly(1).of(_cache).putFileContents(contents("users.png")); inSequence(seq);
			exactly(1).of(_cache).putFolderContents(with(any(FolderContents.class))); inSequence(seq);
		}});*/

	}

/*	private byte[] contents(String fixtureName) throws IOException {
		return my(IO.class).files().readBytes(fixture(fixtureName));
	}
*/
	private File fixture(String fixtureName) {
		return new File(fixturesFolder(), fixtureName);
	}

	private File fixturesFolder() {
		return new File(myClassFile().getParent(), "fixtures");
	}

	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}
	
}