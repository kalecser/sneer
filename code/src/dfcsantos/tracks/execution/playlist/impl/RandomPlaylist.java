package dfcsantos.tracks.execution.playlist.impl;

import java.io.File;
import java.util.Collections;
import java.util.List;

class RandomPlaylist extends AbstractPlaylist {

	RandomPlaylist(File tracksFolder) {
		super(tracksFolder);
	}

	@Override
	public void sortTracks(List<File> tracks) {
		Collections.shuffle(tracks);
	}

}
