package sneer.bricks.softwaresharing.publisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.IO.Filter;

class GitWorkaround {
	
	private static final long AN_ARBITRARY_DATE = 1093489200000L; //26/08/2004 (UTC -3), the publication date of the sovereign computing manifesto. 

	
	static void standardizeLastModifiedDatesWhileWeStillUseGitBecauseGitDoesNotPreserveThem(File rootFolder) {
		Filter standardizer = new Filter() {

			@Override
			public boolean accept(File file) {
				standardizeLastModified(file);
				return true;
			}

			@Override
			public boolean accept(File folderCandidate, String fileName) {
				standardizeLastModified(folderCandidate);
				return true;
			}
		};
		
		my(IO.class).files().listFiles(rootFolder, standardizer , standardizer);
	}


	private static boolean standardizeLastModified(File fileOrFolder) {
		return fileOrFolder.setLastModified(AN_ARBITRARY_DATE);
	}



}
