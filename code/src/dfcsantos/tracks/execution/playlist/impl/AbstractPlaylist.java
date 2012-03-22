package dfcsantos.tracks.execution.playlist.impl;

import static basis.environments.Environments.my;

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

	private int _numberOfTracks;
	private ListIterator<File> _tracksIterator;

	AbstractPlaylist(File tracksFolder) {
		_tracksFolder = tracksFolder;

		load();
	}

	private void load() {
		List<File> tracks = trackFilesFrom(_tracksFolder);
		_numberOfTracks = tracks.size();
		_tracksIterator = tracks.listIterator();
	}

	private List<File> trackFilesFrom(File folder) {
		if (folder == null)
			return Collections.EMPTY_LIST;
			
		List<File> tracks = my(Tracks.class).listMp3FilesFromFolder(folder);
		sortTracks(tracks);
		return tracks;
	}

	void sortTracks(List<File> tracks) { // Sorts tracks alphabetically
		Collections.sort(tracks, new Comparator<File>() { @Override public int compare(File file1, File file2) {
			return file1.getPath().compareTo(file2.getPath());
		}});
	}

	@Override
	public int numberOfTracks() {
		return _numberOfTracks;
	}

	@Override
	public Track nextTrack() {
		if (!_tracksIterator.hasNext()) {
			rescan();
			if (!_tracksIterator.hasNext())
				return null;
		}

		final Track nextTrack = my(Tracks.class).newTrack(_tracksIterator.next());
		_tracksIterator.remove();

		return nextTrack;
	}

	private void rescan() {
		load();
	}

}
