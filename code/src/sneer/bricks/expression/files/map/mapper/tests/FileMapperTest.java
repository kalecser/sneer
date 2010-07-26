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
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import sneer.foundation.lang.Functor;

public class FileMapperTest extends BrickTestWithFiles {

	private final FileMapper _subject = my(FileMapper.class);

	
	@Test (timeout = 3000)
	public void mapFolder() throws Exception {
		Hash hash = _subject.mapFolder(fixturesFolder(), "txt");
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
		Hash hash = _subject.mapFolder(tmpFolder());
		assertStartsWith(new byte[]{16, -91, 39, -65, -72, -88, 88, 10, 27, 82} , hash.bytes.copy()); //Obtained by regression

		createTmpFileWithFileNameAsContent("foo");
		
		hash = _subject.mapFolder(tmpFolder());
		assertStartsWith(new byte[]{16, -91, 39, -65, -72, -88, 88, 10, 27, 82} , hash.bytes.copy()); //Obtained by regression
		
		//fail("this test should have failed");
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
