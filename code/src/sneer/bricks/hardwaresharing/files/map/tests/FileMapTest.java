package sneer.bricks.hardwaresharing.files.map.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class FileMapTest extends BrickTest {

	private final FileMap _subject = my(FileMap.class);

	@Test (timeout = 3000)
	public void putFileInTheMap() {
		File file = fixture("file1.txt");
		Sneer1024 hash = my(Crypto.class).digest(new byte[] { 42 }); 
		_subject.put(file, hash);
		assertEquals(file,_subject.getFile(hash));
	}

	@Test (timeout = 3000)
	public void putFolderContentsInTheMap() {
		final boolean isFolder = true;
		FolderContents folderContents = new FolderContents(immutable(Arrays.asList(new FileOrFolder[]{
			new FileOrFolder("readme.txt", anyReasonableDate(), hash(1), !isFolder),
			new FileOrFolder("src", anyReasonableDate(), hash(2), isFolder),
			new FileOrFolder("docs", anyReasonableDate(), hash(3), isFolder)
		})));
		
		Sneer1024 hash = my(Crypto.class).digest(new byte[] { 42 });
		_subject.putFolderContents(tmpFolder(), folderContents, hash);
		assertEquals(folderContents, _subject.getFolder(hash));
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

	private ImmutableArray<FileOrFolder> immutable(Collection<FileOrFolder> entries) {
		return my(ImmutableArrays.class).newImmutableArray(entries);
	}

	private Sneer1024 hash(int i) {
		return my(Crypto.class).digest(new byte[]{(byte)i});
	}

	private long anyReasonableDate() {
		return System.currentTimeMillis();
	}

}