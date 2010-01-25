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
import sneer.bricks.hardware.cpu.codecs.crypto.Crypto;
import sneer.bricks.hardware.cpu.codecs.crypto.Sneer1024;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Functor;

public class FileMapperTest extends BrickTest {

	private final FileMapper _subject = my(FileMapper.class);
	private final FileMap _fileMap = my(FileMap.class);

	@Test (timeout = 3000)
	public void mapFolder() throws Exception {
		Sneer1024 hash = _subject.mapFolder(fixturesFolder(), "txt");
		FolderContents folderContents = my(FileMap.class).getFolderContents(hash);

		Collection<String> names = my(CollectionUtils.class).map(folderContents.contents, new Functor<FileOrFolder, String>() { @Override public String evaluate(FileOrFolder fileOrFolder) {
			return fileOrFolder.name;
		}});

		assertElementsInAnyOrder(names, "directory1", "directory2", "track4.txt", "track5.txt");
	}

	@Test (timeout = 3000)
	public void clearFolderMapping() throws IOException, MappingStopped {
		final Sneer1024 hashOfFolder = _subject.mapFolder(fixturesFolder());
		assertNotNull(_fileMap.getFolderContents(hashOfFolder));

		final Sneer1024 hashOfFile = my(Crypto.class).digest(fixture("directory1/track1.txt"));
		assertNotNull(_fileMap.getFile(hashOfFile));

		_fileMap.remove(fixturesFolder());
		assertNull(_fileMap.getFolderContents(hashOfFolder));
		assertNull(_fileMap.getFile(hashOfFile));
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
