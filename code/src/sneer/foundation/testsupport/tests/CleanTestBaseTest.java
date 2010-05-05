package sneer.foundation.testsupport.tests;

import java.io.File;

import org.junit.Test;

import sneer.foundation.testsupport.CleanTestBase;

public class CleanTestBaseTest extends CleanTestBase {

	private static File _TMP_FOLDER;

	@Test
	public void consistentTmpFolderName() {
		assertEquals(tmpFolderName(), tmpFolder().getAbsolutePath());
	}

	
	@Test
	public void tmpFolderIsDeleted1() {
		_TMP_FOLDER = tmpFolder();
		assertTrue(_TMP_FOLDER.isDirectory());
		assertTrue(_TMP_FOLDER.exists());
	}

	
	@Test
	public void tmpFolderIsDeleted2() {
		if (wasRunInSeparateVM()) return;
		assertFalse(_TMP_FOLDER.exists());
	}


	private boolean wasRunInSeparateVM() {
		return _TMP_FOLDER == null;
	}
	
}
