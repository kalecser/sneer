package sneer.bricks.expression.files.map.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.arrays.ImmutableArray;

public class FileMapTest extends BrickTest {

	private final FileMap _subject = my(FileMap.class);

	@Test
	public void fileMapping() {
		File file = anySmallFile();
		Hash hash = hash(42);
		
		Map<Hash, File> banana = new ConcurrentHashMap<Hash, File>();
		banana.put(hash, file);
		assertEquals(file, banana.get(hash));
		
		_subject.putFile(file, hash);
		assertEquals(file,_subject.getFile(hash));

		_subject.remove(file);
		assertNull(_subject.getFile(hash));
	}

	@Test
	public void rename() {
		// File structure:
		File folder1 = new File("folder1");
			File subfolder1 = new File(folder1, "subfolder1");
				File file1 = new File(subfolder1, "file1.txt");
				File file2 = new File(subfolder1, "file2.txt");
			File subfolder2 = new File(folder1, "subfolder2");
				File file3 = new File(subfolder2, "file3.txt");
				File subfolder3 = new File(subfolder2, "subfolder3");				
					File file4 = new File(subfolder3, "file4.txt");
			File file5 = new File(folder1, "file5.txt");
		File folder2 = new File("folder2");
			File file6 = new File(folder2, "file6.txt");
			File file7 = new File(folder2, "file7.txt");

		/* Adding file structure to FileMap */

		// Mapping folder1
		List<FileOrFolder> folder1Contents = new ArrayList<FileOrFolder>();

			// Mapping subfolder1
			List<FileOrFolder> subfolder1Contents = new ArrayList<FileOrFolder>();
			Hash file1Hash = hash(1);
			int file1LM = 41;
			subfolder1Contents.add(new FileOrFolder(file1.getName(), file1LM, file1Hash, false));
			_subject.putFile(file1, file1LM, file1Hash);
			Hash file2Hash = hash(2);
			int file2LM = 42;
			subfolder1Contents.add(new FileOrFolder(file2.getName(), file2LM, file2Hash, false));
			_subject.putFile(file2, file2LM, file2Hash);
			Hash subfolder1Hash = hash(10);
			_subject.putFolderContents(subfolder1, newFolderContentsWith(subfolder1Contents), subfolder1Hash);
	
			// Mapping subfolder2
			List<FileOrFolder> subfolder2Contents = new ArrayList<FileOrFolder>();
			Hash file3Hash = hash(3);
			int file3LM = 43;
			subfolder2Contents.add(new FileOrFolder(file3.getName(), file3LM, file3Hash, false));
			_subject.putFile(file3, file3LM, file3Hash);
	
				// Mapping subfolder3
				List<FileOrFolder> subfolder3Contents = new ArrayList<FileOrFolder>();
				Hash file4Hash = hash(4);
				int file4LM = 44;
				subfolder3Contents.add(new FileOrFolder(file4.getName(), file4LM, file4Hash, false));
				_subject.putFile(file4, file4LM, file4Hash);
				Hash subfolder3Hash = hash(30);
				_subject.putFolderContents(subfolder3, newFolderContentsWith(subfolder3Contents), subfolder3Hash);
	
			subfolder2Contents.add(new FileOrFolder(subfolder3.getName(), 30, subfolder3Hash, true));
			Hash subfolder2Hash = hash(20);
			_subject.putFolderContents(subfolder2, newFolderContentsWith(subfolder2Contents), subfolder2Hash);

		folder1Contents.add(new FileOrFolder(subfolder1.getName(), 10, subfolder1Hash, true));
		folder1Contents.add(new FileOrFolder(subfolder2.getName(), 20, subfolder2Hash, true));
		Hash file5Hash = hash(5);
		int file5LM = 45;
		folder1Contents.add(new FileOrFolder(file5.getName(), file5LM, file5Hash, false));
		_subject.putFile(file5, file5LM, file5Hash);
		Hash folder1Hash = hash(100);
		_subject.putFolderContents(folder1, newFolderContentsWith(folder1Contents), folder1Hash);

		// Mapping folder2
		List<FileOrFolder> folder2Contents = new ArrayList<FileOrFolder>();
		Hash file6Hash = hash(6);
		int file6LM = 46;
		_subject.putFile(file6, file6LM, file6Hash);
		folder2Contents.add(new FileOrFolder(file6.getName(), file6LM, file6Hash, false));
		Hash file7Hash = hash(7);
		int file7LM = 47;
		_subject.putFile(file7, file7LM, file7Hash);
		folder2Contents.add(new FileOrFolder(file7.getName(), file7LM, file7Hash, false));
		Hash folder2Hash = hash(200);
		_subject.putFolderContents(folder2, newFolderContentsWith(folder2Contents), folder2Hash);

		// Renaming: folder1 --> newFolder1
		File newFolder1 = new File("newFolder1");
		_subject.rename(folder1, newFolder1);

		assertEquals(folder1Hash, _subject.getHash(newFolder1));

		assertEquals(subfolder1Hash, _subject.getHash(new File("newFolder1/subfolder1")));
		assertNull(_subject.getHash(file1));
		assertFileWasRenamed("newFolder1/subfolder1/file1.txt", file1LM, file1Hash);
		assertNull(_subject.getHash(file2));
		assertFileWasRenamed("newFolder1/subfolder1/file2.txt", file2LM, file2Hash);

		assertEquals(subfolder2Hash, _subject.getHash(new File("newFolder1/subfolder2")));
		assertNull(_subject.getHash(file3));
		assertFileWasRenamed("newFolder1/subfolder2/file3.txt", file3LM, file3Hash);

		assertEquals(subfolder3Hash, _subject.getHash(new File("newFolder1/subfolder2/subfolder3")));
		assertNull(_subject.getHash(file4));
		assertFileWasRenamed("newFolder1/subfolder2/subfolder3/file4.txt", file4LM, file4Hash);

		assertNull(_subject.getHash(file5));
		assertFileWasRenamed("newFolder1/file5.txt", file5LM, file5Hash);

		// Renaming: newFolder1/subfolder2 --> newFolder1/newSubfolder2
		_subject.rename(new File("newFolder1/subfolder2"), new File("newFolder1/newSubfolder2"));

		assertNull(_subject.getHash(new File("newFolder1/subfolder2/file3.txt")));
		assertFileWasRenamed("newFolder1/newSubfolder2/file3.txt", file3LM, file3Hash);
		assertFileWasRenamed("newFolder1/newSubfolder2/subfolder3/file4.txt", file4LM, file4Hash);

		// Renaming: folder2/file6.txt --> newFolder2/file6.txt
		_subject.rename(file6, new File("newFolder2/file6.txt"));
		assertFileWasRenamed("newFolder2/file6.txt", file6LM, file6Hash);
	}

	private void assertFileWasRenamed(String newFileName, int lastModified, Hash hash) {
		File newFile = new File(newFileName);
		assertEquals(hash, _subject.getHash(newFile));
		assertEquals(newFile, _subject.getFile(hash));
		assertEquals(lastModified, _subject.getLastModified(newFile));
	}

	private File anySmallFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

	private Hash hash(int b) {
		return my(Crypto.class).digest(new byte[] { (byte) b });
	}

	private FolderContents newFolderContentsWith(List<FileOrFolder> contents) {
		return new FolderContents(new ImmutableArray<FileOrFolder>((contents)));
	}

}
