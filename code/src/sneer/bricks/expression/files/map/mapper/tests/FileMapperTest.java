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
		File newFolder = newFolder("newFolder");
		Hash hash = _subject.mapFolder(newFolder);
		assertStartsWith(new byte[]{-49, -125, -31, 53, 126, -17, -72, -67, -15, 84} , hash.bytes.copy()); //Obtained by regression

		File file = createTmpFileWithFileNameAsContent("newFolder/foo");
		file.setLastModified(42);
		hash = _subject.mapFolder(newFolder);
		assertStartsWith(new byte[]{107, 36, 53, 46, 121, -47, 119, 52, 70, -74} , hash.bytes.copy()); //Obtained by regression
		
		file.delete();
		hash = _subject.mapFolder(newFolder);
		assertStartsWith(new byte[]{-49, -125, -31, 53, 126, -17, -72, -67, -15, 84} , hash.bytes.copy()); //Obtained by regression
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
