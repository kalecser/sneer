package dfcsantos.tracks.execution.playlist.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.execution.playlist.Playlist;

abstract class AbstractPlaylist implements Playlist {

	private final File _tracksFolder;
	private ListIterator<File> _trackIterator;

	AbstractPlaylist(File tracksFolder) {
		_tracksFolder = tracksFolder;
		initTrackIterator();
	}

	private void initTrackIterator() {
		_trackIterator = trackFiles().listIterator();	
	}

	private List<File> trackFiles() {
		List<File> tracks = my(Tracks.class).listMp3FilesFromFolder(_tracksFolder);
		sortTracks(tracks);

		return tracks;
	}

	void sortTracks(List<File> tracks) { // Sorts tracks alphabetically
		Collections.sort(tracks, new Comparator<File>() { @Override public int compare(File file1, File file2) {
			return file1.getPath().compareTo(file2.getPath());
		}});
	}

	@Override
	public Track nextTrack() {
		if (!_trackIterator.hasNext()) {
			rescan();
			if (!_trackIterator.hasNext())
				return null;
		}

		final Track nextTrack = my(Tracks.class).newTrack(_trackIterator.next());
		_trackIterator.remove();

		return nextTrack;
	}

	private void rescan() {
		initTrackIterator();
	}

}
