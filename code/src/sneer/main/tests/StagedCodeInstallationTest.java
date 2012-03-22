package sneer.main.tests;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import basis.testsupport.CleanTestBase;

import sneer.main.SneerVersionUpdater;


public class StagedCodeInstallationTest extends CleanTestBase {
	
	@Test
	public void stagedCodeInstallation() throws IOException {
		createTmpFiles(
			"src/sneer/main/Sneer.java",
			"bin/sneer/main/Sneer.class",
			"src/sneer/main/SneerVersionUpdater.java",
			"bin/sneer/main/SneerVersionUpdater.class",
			"bin/sneer/main/SneerVersionUpdater$ExclusionFilter.class",
			"src/sneer/main/SneerCodeFolders.java",
			"bin/sneer/main/SneerCodeFolders.class",
			"src/toBeDeleted.java",
			"bin/toBeDeleted.class",
			
			"backup/2000-01-01-00-00-00", //Previous backup
			
			"stage/src/foo.java",
			"stage/bin/foo.class"
		);
		
		SneerVersionUpdater.installNewVersionIfPresent(tmpFolder("stage"), "2008-12-31-23-59-59", tmpFolder());
		
		assertTmpFilesExist(
			"backup/2000-01-01-00-00-00",
				
			"backup/2008-12-31-23-59-59/src/sneer/main/Sneer.java",
			"backup/2008-12-31-23-59-59/bin/sneer/main/Sneer.class",
			"backup/2008-12-31-23-59-59/src/toBeDeleted.java",
			"backup/2008-12-31-23-59-59/bin/toBeDeleted.class",

			"src/sneer/main/Sneer.java", //Preserved
			"bin/sneer/main/Sneer.class",
			"src/sneer/main/SneerVersionUpdater.java",
			"bin/sneer/main/SneerVersionUpdater.class",
			"bin/sneer/main/SneerVersionUpdater$ExclusionFilter.class",
			"src/sneer/main/SneerCodeFolders.java",
			"bin/sneer/main/SneerCodeFolders.class",

			"src/foo.java", //Copied
			"bin/foo.class"
		);
		
		assertTmpFilesDontExist(
			"stage",
			"src/toBeDeleted.java",
			"bin/toBeDeleted.class"			
		);
		
	}

	
	private File tmpFolder(String folderName) {
		return new File(tmpFolder(), folderName);
	}
	
}
