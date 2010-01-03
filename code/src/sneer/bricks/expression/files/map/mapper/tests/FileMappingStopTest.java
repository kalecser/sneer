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
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;

public class FileMappingStopTest extends BrickTest {

	private final FileMapper _subject = my(FileMapper.class);
	@Bind private final FileMap _fileMap = mock(FileMap.class);

	@Test (timeout = 3000, expected = MappingStopped.class)
	public void mapFolder() throws Exception {
		checking(new Expectations() {{
			oneOf(_fileMap).putFile(with(any(File.class)), with(any(Sneer1024.class)));
				will(new CustomAction("Call stopFolderMapping") { @Override public Object invoke(Invocation invocation) throws Throwable {
					_subject.stopFolderMapping(fixturesFolder());
					return null;
				}});
			oneOf(_fileMap).remove(fixturesFolder());
		}});
		
		_subject.mapFolder(fixturesFolder(), "txt");
	}

	
	private File fixturesFolder() {
		return new File(myClassFile().getParent(), "fixtures");
	}

	
	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}
