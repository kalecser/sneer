package dfcsantos.tracks.rejected.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.software.bricks.statestore.BrickStateStore;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

class RejectedTracksKeeperImpl implements RejectedTracksKeeper {

	private static final BrickStateStore _store = my(BrickStateStore.class);

	private final List<Track> _rejectedTracks = new ArrayList<Track>();

	RejectedTracksKeeperImpl() {
		restore();
	}

	@Override
	public void reject(Track victim) {
		_rejectedTracks.add(victim);
		save();
	}

	@Override
	public boolean isRejected(Track suspect) {
		return rejected().contains(suspect);
	}

	@Override
	public List<Track> rejected() {
		return Collections.unmodifiableList(_rejectedTracks);
	}

	private void restore() {
		List<String> trackFilePaths =
			(List<String>) my(BrickStateStore.class).readObjectFor(RejectedTracksKeeper.class, getClass().getClassLoader());

		if (trackFilePaths == null) return;

		for (String trackFilePath : trackFilePaths) {
			_rejectedTracks.add(my(Tracks.class).newTrack(new File(trackFilePath)));
		}
	}

	private void save() {
		List<String> trackFilePathsToPersist = new ArrayList<String>();
		for (Track trackToPersist : _rejectedTracks) {
			trackFilePathsToPersist.add(trackToPersist.file().getPath());
		}

		_store.writeObjectFor(RejectedTracksKeeper.class, trackFilePathsToPersist);
	}

}
