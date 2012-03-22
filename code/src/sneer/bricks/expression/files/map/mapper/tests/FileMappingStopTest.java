package sneer.bricks.expression.files.map.mapper.tests;

import static basis.environments.Environments.my;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import basis.brickness.testsupport.Bind;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class FileMappingStopTest extends BrickTestBase {

	@Bind private final FileMap _fileMap = mock(FileMap.class);
	private final FileMapper _subject = my(FileMapper.class);

	@Test (timeout = 3000, expected = MappingStopped.class)
	public void mapFolder() throws Exception {
		final File fixturesFolder = new File(myClassFile().getParent(), "fixtures");

		checking(new Expectations() {{
			allowing(_fileMap).getHash(with(any(String.class)));
			oneOf(_fileMap).putFile(with(any(String.class)), with(any(Long.class)), with(any(Hash.class)));
				will(new CustomAction("Call stopFolderMapping") { @Override public Object invoke(Invocation invocation) throws Throwable {
					_subject.stopMapping(fixturesFolder);
					return null;
				}});
			oneOf(_fileMap).remove(fixturesFolder.getAbsolutePath());
		}});

		_subject.mapFileOrFolder(fixturesFolder, "txt");
	}

	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}
