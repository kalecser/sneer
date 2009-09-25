package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import sneer.bricks.hardware.io.IO;
import dfcsantos.wusic.Track;

class RecursiveFolderPlaylist implements Iterator<Track> {

	private final List<File> _trackFiles;
	private final Random _random = new Random();
	private int _currentIndex;

	RecursiveFolderPlaylist(File folder) {
		_trackFiles = searchTracks(folder);
	}

	private List<File> searchTracks(File folder) {
		return new ArrayList<File>(my(IO.class).files().listFiles(folder, new String[] {"mp3","MP3"}, true));
	}

	private Track randomTrack() {
		_currentIndex = _random.nextInt(_trackFiles.size());
		return new TrackImpl(_trackFiles.get(_currentIndex));
	}

	@Override
	public boolean hasNext() {
		return !_trackFiles.isEmpty();
	}

	@Override
	public synchronized Track next() {
		return randomTrack();
	}

	@Override
	public synchronized void remove() {
		_trackFiles.remove(_currentIndex);
	}
}
