package sneer.main.tests;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.foundation.testsupport.CleanTestBase;
import sneer.main.Sneer;


public class StagedCodeInstallationTest extends CleanTestBase {

	/*

	Prepare staged (Me Too)

		delete stage
		delete stageTmp

		stageTmp
			src copy
			bin compile
			
		stageTmp -> stage



	(bin)sneer.main.Sneer:

		if !stage.exists return;
		
		backup src
		backup bin
			
		delete src
		delete bin except for this
			
		stage/src -copy-> src
		stage/bin -copy-> bin
		
		stage -> stageToDelete
		delete stageToDelete

	*/	

	
	@Test
	public void stagedCodeInstallation() throws IOException {
		createTmpFiles(
			"current/src/sneer/main/Sneer.java",
			"current/bin/sneer/main/Sneer.class",
			"current/bin/sneer/main/Sneer$ExclusionFilter.class",
			"current/src/sneer/main/SneerCodeFolders.java",
			"current/bin/sneer/main/SneerCodeFolders.class",
			"current/src/toBeDeleted.java",
			"current/bin/toBeDeleted.class",
			
			"backup/2000-01-01-00-00-00", //Previous backup
			
			"stage/src/foo.java",
			"stage/bin/foo.class"
		);
		
		Sneer.installStagedCodeIfNecessary(tmpFolder("stage"), tmpFolder("backup/2008-12-31-23-59-59"), tmpFolder("current"));
		
		assertTmpFilesExist(
			"backup/2000-01-01-00-00-00",
				
			"backup/2008-12-31-23-59-59/src/sneer/main/Sneer.java",
			"backup/2008-12-31-23-59-59/bin/sneer/main/Sneer.class",
			"backup/2008-12-31-23-59-59/src/toBeDeleted.java",
			"backup/2008-12-31-23-59-59/bin/toBeDeleted.class",

			"current/src/sneer/main/Sneer.java", //Preserved
			"current/bin/sneer/main/Sneer.class",
			"current/bin/sneer/main/Sneer$ExclusionFilter.class",
			"current/src/sneer/main/SneerCodeFolders.java",
			"current/bin/sneer/main/SneerCodeFolders.class",

			"current/src/foo.java", //Copied
			"current/bin/foo.class"
		);
		
		assertTmpFilesDontExist(
			"stage",
			"current/src/toBeDeleted.java",
			"current/bin/toBeDeleted.class"			
		);
		
	}

	
	private File tmpFolder(String folderName) {
		return new File(tmpFolder(), folderName);
	}
	
}
