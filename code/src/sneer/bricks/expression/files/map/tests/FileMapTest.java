package sneer.bricks.expression.files.map.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class FileMapTest extends BrickTest {

	private final FileMap _subject = my(FileMap.class);

	@Test
	public void fileMapping() {
		File file = anySmallFile();
		Sneer1024 hash = my(Crypto.class).digest(new byte[] { 42 }); 
		_subject.putFile(file, hash);
		assertEquals(file,_subject.getFile(hash));

		_subject.remove(file);
		assertNull(_subject.getFile(hash));
	}

	private File anySmallFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}
