package dfcsantos.tracks.playlist.impl;

import java.io.File;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import dfcsantos.tracks.Track;

class SequentialPlaylist extends AbstractPlaylist {

	private final ListIterator<File> _tracksIterator;

	SequentialPlaylist(File tracksFolder) {
		super(tracksFolder);
		_tracksIterator = trackFiles().listIterator();
	}

	@Override
	public Track previousTrack() {
		try {
			return new TrackImpl(_tracksIterator.previous());
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	@Override
	public Track nextElement() {
		return new TrackImpl(_tracksIterator.next());
	}

}
