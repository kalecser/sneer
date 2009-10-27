package dfcsantos.tracks.playlist.impl;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SequentialPlaylist extends AbstractPlaylist {

	SequentialPlaylist(File tracksFolder) {
		super(tracksFolder);
	}

	@Override
	void setTracksOrder(List<File> tracks) {
		Collections.sort(tracks, new Comparator<File>() { @Override public int compare(File file1, File file2) {
			return file1.compareTo(file2);
		}});
	}

}
