package sneer.bricks.expression.files.map.mapper.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.Functor;

public class FileMapperTest extends BrickTestBase {

	private final FileMapper _subject = my(FileMapper.class);

	
	@Test (timeout = 3000)
	public void mapFolder() throws Exception {
		Hash hash = _subject.mapFileOrFolder(fixturesFolder(), "txt");
		FolderContents folderContents = my(FileMap.class).getFolderContents(hash);

		Collection<String> names = my(CollectionUtils.class).map(folderContents.contents, new Functor<FileOrFolder, String>() { @Override public String evaluate(FileOrFolder fileOrFolder) {
			return fileOrFolder.name;
		}});

		assertContentsInAnyOrder(names, "directory1", "directory2", "track4.txt", "track5.txt");
		
		Hash hashOfFile = hashOfFile("directory1/track1.txt");
		assertNotNull(my(FileMap.class).getFile(hashOfFile));
	}


	private Hash hashOfFile(String fileMane) throws IOException {
		Hash hashOfFile = my(Crypto.class).digest(fixture(fileMane));
		return hashOfFile;
	}

	
	@Test (timeout = 3000)
	public void remappingAChangedFolder() throws IOException, MappingStopped {
		File newFolder = newFolder("newFolder");
		byte[] hashForEmptyFolder = new byte[]{-49, -125, -31, 53, 126, -17, -72, -67, -15, 84}; //Obtained by regression
		mapAndAssert(newFolder, hashForEmptyFolder);

		File foo = createTmpFileWithFileNameAsContent("newFolder/foo.txt", 42);
		File bar = createTmpFileWithFileNameAsContent("newFolder/bar.txt", 42);
		mapAndAssert(newFolder, new byte[]{-3, -35, 23, -71, -35, 69, -82, 114, 86, 0}); //Obtained by regression
		
		foo.delete();
		mapAndAssert(newFolder, new byte[]{-106, -126, 47, -77, -128, 77, -105, -128, 14, -24}); //Obtained by regression
		assertFalse(isMapped(foo));
		assertTrue(isMapped(bar));

		bar.delete();
		mapAndAssert(newFolder, hashForEmptyFolder);
		assertFalse(isMapped(bar));
	}

	
	@Test (timeout = 3000)
	public void lastModifiedDoesNotAffectHash() throws IOException, MappingStopped {
		File newFolder = newFolder("newFolder");

		File foo = createTmpFileWithFileNameAsContent("newFolder/foo.txt", 0);
		byte[] expectedHash = map(newFolder).bytes.copy(); 
		foo.setLastModified(2000);
		mapAndAssert(newFolder, expectedHash);
		foo.setLastModified(2050);
		mapAndAssert(newFolder, expectedHash);
	}


	private boolean isMapped(File file) {
		return my(FileMap.class).getHash(file.getAbsolutePath()) != null;
	}


	protected File createTmpFileWithFileNameAsContent(String fileName, long lastModified)	throws IOException {
		File result = super.createTmpFileWithFileNameAsContent(fileName);
		result.setLastModified(lastModified);
		return result;
	}


	private void mapAndAssert(File fileOrFolder, byte[] expectedHashStart) throws MappingStopped, IOException {
		Hash hash = map(fileOrFolder);
		assertStartsWith(expectedHashStart, hash.bytes.copy());
	}


	private Hash map(File fileOrFolder) throws MappingStopped, IOException {
		return _subject.mapFileOrFolder(fileOrFolder);
	}


	private File newFolder(String name) {
		File result = newTmpFile(name);
		result.mkdir();
		return result;
	}

	
	private File fixture(String name) {
		return new File(fixturesFolder(), name);
	}

	
	private File fixturesFolder() {
		return new File(myClassFile().getParent(), "fixtures");
	}

	
	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}
