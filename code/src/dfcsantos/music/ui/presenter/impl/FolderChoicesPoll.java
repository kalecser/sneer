package dfcsantos.music.ui.presenter.impl;
import static basis.environments.Environments.my;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;

public class FolderChoicesPoll {
	
	private final Path rootFolder;
	private final Light error = my(BlinkingLights.class).prepare(LightType.ERROR);
	
	
	public FolderChoicesPoll(Path rootFolder) {
		this.rootFolder = rootFolder.toAbsolutePath();
	}
	
	
	public List<String> result() {
		List<String> ret = new ArrayList<String>();
		accumulateSubFolders(rootFolder, ret);
		return ret;
	}
	
	
	private void accumulateSubFolders(Path folder, List<String> result) {
		try {
			tryToAccumulateSubFolders(folder, result);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOnIfNecessary(error, "Error Reading Tracks Folder", "There might be a probem with the track folder device: " + rootFolder, e, 10000);
		}
	}


	private void tryToAccumulateSubFolders(Path folder, List<String> result)	throws IOException {
		try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder)) {
			for (Path entry : entries)
				if (Files.isDirectory(entry))
					accumulateWithSubFolders(entry, result);
		}
	}

	
	private void accumulateWithSubFolders(Path entry, List<String> result) {
		 result.add(rootFolder.relativize(entry.toAbsolutePath()).toString());
		 accumulateSubFolders(entry, result);
	}

}