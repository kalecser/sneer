package dfcsantos.tracks.playlist.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.playlist.Playlist;

abstract class AbstractPlaylist implements Playlist, Enumeration<Track> {

	private final File _tracksFolder;
	private List<File> _trackFiles;

	private final Light _noTracksFound = my(BlinkingLights.class).prepare(LightType.WARN);

	protected AbstractPlaylist(File tracksFolder) {
		_tracksFolder = tracksFolder;
		loadTracks();
	}

	protected List<File> trackFiles() {
		return _trackFiles;
	}

	protected List<File> searchTracks(File folder) {
		return new ArrayList<File>(my(IO.class).files().listFiles(folder, new String[] {"mp3","MP3"}, true));
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

	protected void rescan() {
		loadTracks();
	}

}
