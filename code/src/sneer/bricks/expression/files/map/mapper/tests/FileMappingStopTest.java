package sneer.bricks.expression.files.map.mapper.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;

public class FileMappingStopTest extends BrickTest {

	@Bind private final FileMap _fileMap = mock(FileMap.class);
	private final FileMapper _subject = my(FileMapper.class);

	@Test (timeout = 3000, expected = MappingStopped.class)
	public void mapFolder() throws Exception {
		final File fixturesFolder = new File(myClassFile().getParent(), "fixtures");

		checking(new Expectations() {{
			oneOf(_fileMap).getHash(with(any(File.class)));
			oneOf(_fileMap).getLastModified(with(any(File.class))); will(returnValue(-1L));
			oneOf(_fileMap).putFile(with(any(File.class)), with(any(Hash.class)));
				will(new CustomAction("Call stopFolderMapping") { @Override public Object invoke(Invocation invocation) throws Throwable {
					_subject.stopFolderMapping(fixturesFolder);
					return null;
				}});
			oneOf(_fileMap).remove(fixturesFolder);
		}});

		_subject.mapFolder(fixturesFolder, "txt");
	}

	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}
