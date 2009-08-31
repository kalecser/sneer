package sneer.bricks.softwaresharing.publisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.reader.FileReader;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.softwaresharing.publisher.SourcePublisher;
import sneer.bricks.softwaresharing.publisher.SrcFolderHash;

class SourcePublisherImpl implements SourcePublisher {
	
	private static final long AN_ARBITRARY_DATE = 1093489200000L; //26/08/2004 (UTC -3), the publication date of the sovereign computing manifesto. 
	private final Light _errorLight = my(BlinkingLights.class).prepare(LightType.ERROR);

	
	@Override
	public void publishSourceFolder() {
		standardizeLastModifiedDatesWhileWeStillUseGitBecauseGitDoesNotPreserveThem();
		
		Sneer1024 hash;
		try {
			hash = my(FileReader.class).readIntoTheFileCache(platformSrcFolder());
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(_errorLight, "Error reading your source folder.", "There was trouble trying to read your source folder in order to publish your bricks for your peers. See log for details.", e);
			return;
		}
		
		my(TupleSpace.class).publish(new SrcFolderHash(hash));
	}


	private void standardizeLastModifiedDatesWhileWeStillUseGitBecauseGitDoesNotPreserveThem() {
		Set<File> foldersVisited = new HashSet<File>();
		
		Iterator<File> srcFiles = srcFiles();
		while (srcFiles.hasNext()) {
			File file = srcFiles.next();
			standardizeLastModified(file);

			File folder = file.getParentFile();
			if (foldersVisited.add(folder))
				standardizeLastModified(folder);
		}
	}


	private boolean standardizeLastModified(File fileOrFolder) {
		return fileOrFolder.setLastModified(AN_ARBITRARY_DATE);
	}


	private Iterator<File> srcFiles() {
		return my(IO.class).files().iterate(platformSrcFolder(), null, true);
	}


	private File platformSrcFolder() {
		return my(FolderConfig.class).platformSrcFolder().get();
	}
	
}
