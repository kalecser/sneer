package dfcsantos.tracks.rejected.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.software.bricks.statestore.BrickStateStore;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

class RejectedTracksKeeperImpl implements RejectedTracksKeeper {

	private static final BrickStateStore _store = my(BrickStateStore.class);

	private final List<Sneer1024> _rejectedTrackHashes = Collections.synchronizedList(new ArrayList<Sneer1024>());

	RejectedTracksKeeperImpl() {
		restore();
	}

	@Override
	public void reject(Sneer1024 hash) {
		_rejectedTrackHashes.add(hash);
		save();
	}

	@Override
	public boolean isRejected(Sneer1024 hash) {
		return _rejectedTrackHashes.contains(hash);
	}

	private void restore() {
		List<Sneer1024> restoredTrackHashes =
			(List<Sneer1024>) my(BrickStateStore.class).readObjectFor(RejectedTracksKeeper.class, getClass().getClassLoader());

		if (restoredTrackHashes == null) return;

		for (Sneer1024 restoredHash : restoredTrackHashes) {
			_rejectedTrackHashes.add(restoredHash);
		}
	}

	private void save() {
		List<Sneer1024> trackHashesToPersist = new ArrayList<Sneer1024>();
		for (Sneer1024 trackHashToPersist : _rejectedTrackHashes) {
			trackHashesToPersist.add(trackHashToPersist);
		}

		_store.writeObjectFor(RejectedTracksKeeper.class, trackHashesToPersist);
	}

}
