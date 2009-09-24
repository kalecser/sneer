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
			"code/src/sneer/main/Sneer.java",
			"code/bin/sneer/main/Sneer.class",
			"code/bin/sneer/main/ExclusionFilter.class",
			"code/src/sneer/main/SneerCodeFolders.java",
			"code/bin/sneer/main/SneerCodeFolders.class",
			"code/src/toBeDeleted.java",
			"code/bin/toBeDeleted.class",
			
			"backup/2000-01-01-00-00-00", //Previous backup
			
			"stage/src/foo.java",
			"stage/bin/foo.class"
		);
		
		Sneer.installStagedCode(tmpFolder("stage"), tmpFolder("backup/2008-12-31-23-59-59"), tmpFolder("code"));
		
		assertTmpFilesExist(
			"backup/2000-01-01-00-00-00",
				
			"backup/2008-12-31-23-59-59/src/sneer/main/Sneer.java",
			"backup/2008-12-31-23-59-59/bin/sneer/main/Sneer.class",
			"backup/2008-12-31-23-59-59/src/toBeDeleted.java",
			"backup/2008-12-31-23-59-59/bin/toBeDeleted.class",

			"code/src/sneer/main/Sneer.java", //Preserved
			"code/bin/sneer/main/Sneer.class",

			"code/src/foo.java", //Copied
			"code/bin/foo.class"
		);
		
		assertTmpFilesDontExist(
			"stage",
			"code/src/toBeDeleted.java",
			"code/bin/toBeDeleted.class"			
		);
		
	}

	
	private File tmpFolder(String folderName) {
		return new File(tmpFolder(), folderName);
	}
	
}
