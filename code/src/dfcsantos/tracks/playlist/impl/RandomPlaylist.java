package dfcsantos.tracks.playlist.impl;

import java.io.File;
import java.util.Random;

import dfcsantos.tracks.Track;

class RandomPlaylist extends AbstractPlaylist {

	private static final Random _random = new Random();

	RandomPlaylist(File tracksFolder) {
		super(tracksFolder);
	}

	@Override
	public Track nextElement() {
		 // Note that the returned element is removed from the list
		return new TrackImpl(trackFiles().remove(_random.nextInt(trackFiles().size())));
	}

}
