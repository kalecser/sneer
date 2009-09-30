package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import sneer.bricks.hardware.io.IO;
import dfcsantos.wusic.Track;

class RecursiveFolderPlaylist implements Enumeration<Track> {

	private final File _tracksFolder;
	private List<File> _trackFiles;

	private static final Random _random = new Random();

	RecursiveFolderPlaylist(File folder) {
		_tracksFolder = folder;
		loadTracks();
	}

	private List<File> searchTracks(File folder) {
		return new ArrayList<File>(my(IO.class).files().listFiles(folder, new String[] {"mp3","MP3"}, true));
	}

	private void loadTracks() {
		_trackFiles = searchTracks(_tracksFolder);		
	}

	private Track randomTrack() {
		return new TrackImpl(_trackFiles.remove(_random.nextInt(_trackFiles.size())));
	}

	@Override
	public boolean hasMoreElements() {
		return !_trackFiles.isEmpty();
	}

	@Override
	public synchronized Track nextElement() {
		Track nextElement = randomTrack();
		return nextElement;
	}

	void rescan() {
		loadTracks();
	}
}
