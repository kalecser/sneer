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

	private final List<File> _trackFiles;
	private final Random _random = new Random();
	
	
	RecursiveFolderPlaylist(File folder) {
		_trackFiles = searchSongs(folder);
	}
	
	
	private List<File> searchSongs(File folder) {
		return new ArrayList<File>(my(IO.class).files().listFiles(folder, new String[] {"mp3","MP3"}, true));
	}
	
	
	private Track randomTrack() {
		int indexOfRandom = _random.nextInt( _trackFiles.size());
		return new TrackImpl(_trackFiles.get(indexOfRandom));
	}

	
	@Override
	public boolean hasMoreElements() {
		return !_trackFiles.isEmpty();
	}
	
	
	@Override
	public Track nextElement() {
		return randomTrack();
	}

}
