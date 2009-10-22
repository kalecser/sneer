package dfcsantos.tracks.playlist.impl;

import java.io.File;
import java.util.ListIterator;

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
	public boolean hasMoreElements() {
		return _tracksIterator.hasNext();
	}

	@Override
	public Track nextElement() {
		// Note that the returned element is NOT removed from the list like in RandomPlaylist
		return new TrackImpl(_tracksIterator.next());
	}

	@Override
	void rescan() {
		// One new iterator is used for each iteration sequence 
		initIterator();
	}

}
