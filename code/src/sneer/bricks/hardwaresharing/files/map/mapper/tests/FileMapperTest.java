package sneer.bricks.hardwaresharing.files.map.mapper.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.map.mapper.FileMapper;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Functor;

public class FileMapperTest extends BrickTest {

	private final FileMapper _subject = my(FileMapper.class);

	@Test (timeout = 3000)
	public void mapFolder() throws IOException {
		Sneer1024 hash = _subject.map(fixturesFolder(), "txt");
		FolderContents folderContents = my(FileMap.class).getFolder(hash);

		Collection<String> names = my(CollectionUtils.class).map(folderContents.contents, new Functor<FileOrFolder, String>() { @Override public String evaluate(FileOrFolder fileOrFolder) {
			return fileOrFolder.name;
		}});

		assertElementsInAnyOrder(names, "directory1", "directory2", "track4.txt", "track5.txt");
	}

	private File fixturesFolder() {
		return new File(myClassFile().getParent(), "fixtures");
	}

	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}
