package dfcsantos.tracks.playlist.impl;

import java.io.File;
import java.util.ListIterator;

import dfcsantos.tracks.Track;

class SequentialPlaylist extends AbstractPlaylist {

	private final ListIterator<File> _tracksIterator;

	SequentialPlaylist(File tracksFolder) {
		super(tracksFolder);
		_tracksIterator = trackFiles().listIterator();
	}

	@Override
	public Track nextElement() {
		Track track = new TrackImpl(_tracksIterator.next());
		_tracksIterator.remove();
		return track;
	}

}
