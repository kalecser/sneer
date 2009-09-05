package sneer.bricks.hardwaresharing.files.reader.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.hardwaresharing.files.cache.FileCache;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.reader.FileReader;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.foundation.brickness.testsupport.Bind;

public class FileReaderTest extends TestThatUsesLogger {

	@Bind private final FileCache _cache = mock(FileCache.class); 
	
	private final FileReader _subject = my(FileReader.class);

	
	@Test (timeout = 3000)
	public void readSmallFileToTheCache() throws IOException {
		checking(new Expectations(){{
			exactly(1).of(_cache).putFileContents(contents("file1.txt"));
		}});
		_subject.readIntoTheFileCache(fixture("file1.txt"));
	}

	@Test (timeout = 3000)
	public void readFolderToTheCache() throws IOException {
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
		}});
		_subject.readIntoTheFileCache(fixturesFolder());
	}

	private byte[] contents(String fixtureName) throws IOException {
		return my(IO.class).files().readBytes(fixture(fixtureName));
	}

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