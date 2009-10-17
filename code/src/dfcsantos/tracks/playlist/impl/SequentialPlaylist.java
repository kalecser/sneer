package dfcsantos.tracks.playlist.impl;

import java.io.File;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import dfcsantos.tracks.Track;

class SequentialPlaylist extends AbstractPlaylist {

	private ListIterator<File> _tracksIterator;

	SequentialPlaylist(File tracksFolder) {
		super(tracksFolder);
		initIterator();
	}

	private void initIterator() {
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
	public boolean hasMoreElements() {
		return _tracksIterator.hasNext();
	}

	@Override
	public Track nextElement() {
		return new TrackImpl(_tracksIterator.next());
	}

	@Override
	protected void rescan() {
		initIterator();
	}

}
