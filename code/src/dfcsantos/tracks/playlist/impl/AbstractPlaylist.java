package dfcsantos.tracks.playlist.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Enumeration;
import java.util.List;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.playlist.Playlist;

abstract class AbstractPlaylist implements Playlist, Enumeration<Track> {

	private final File _tracksFolder;
	private List<File> _trackFiles;

	private final Light _noTracksFound = my(BlinkingLights.class).prepare(LightType.WARN);

	AbstractPlaylist(File tracksFolder) {
		_tracksFolder = tracksFolder;
		loadTracks();
	}

	List<File> trackFiles() {
		return _trackFiles;
	}

	List<File> searchTracks(File folder) {
		return my(Tracks.class).listMp3FilesFromFolder(folder);
	}

	private void loadTracks() {
		_trackFiles = searchTracks(_tracksFolder);		
	}

	@Override
	public Track nextTrack() {
		if (!hasMoreElements()) {
			rescan();
			if (!hasMoreElements()) {
				my(BlinkingLights.class).turnOnIfNecessary(_noTracksFound, "No Tracks Found", "Please choose a folder with MP3 files in it or in its subfolders (Wusic > File > Configure Root Track Folder).");
				return null;
			}
		}
		my(BlinkingLights.class).turnOffIfNecessary(_noTracksFound);
		return nextElement();
	}

	@Override
	public boolean hasMoreElements() {
		return !trackFiles().isEmpty();
	}

	void rescan() {
		loadTracks();
	}

}
