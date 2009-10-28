package dfcsantos.tracks.rejected.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dfcsantos.tracks.Track;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

class RejectedTracksKeeperImpl implements RejectedTracksKeeper {

	private final List<Track> _rejectedTracks = new ArrayList<Track>();

	@Override
	public void reject(Track victim) {
		_rejectedTracks.add(victim);
	}

	@Override
	public boolean isRejected(Track suspect) {
		return _rejectedTracks.contains(suspect);
	}

	@Override
	public List<Track> rejected() {
		return Collections.unmodifiableList(_rejectedTracks);
	}

}
