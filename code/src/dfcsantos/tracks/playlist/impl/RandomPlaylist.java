package dfcsantos.tracks.playlist.impl;

import java.io.File;
import java.util.Random;

import dfcsantos.tracks.Track;

class RandomPlaylist extends AbstractPlaylist {

	private static final Random _random = new Random();

	private Track _lastTrack;

	RandomPlaylist(File tracksFolder) {
		super(tracksFolder);
	}

	@Override
	public Track previousTrack() {
		return _lastTrack;
	}

	@Override
	public Track nextElement() {
		_lastTrack = new TrackImpl(trackFiles().remove(_random.nextInt(trackFiles().size())));
		return _lastTrack;
	}

}
