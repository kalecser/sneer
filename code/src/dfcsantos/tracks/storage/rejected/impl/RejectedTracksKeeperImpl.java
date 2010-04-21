package dfcsantos.tracks.storage.rejected.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.bricks.statestore.BrickStateStore;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

class RejectedTracksKeeperImpl implements RejectedTracksKeeper {

	private static final BrickStateStore _store = my(BrickStateStore.class);

	private final List<Hash> _rejectedTrackHashes = Collections.synchronizedList(new ArrayList<Hash>());

	RejectedTracksKeeperImpl() {
		restore();
	}

	@Override
	public void reject(Hash hash) {
		_rejectedTrackHashes.add(hash);
		save();
	}

	@Override
	public boolean isRejected(Hash hash) {
		return _rejectedTrackHashes.contains(hash);
	}

	private void restore() {
		List<Hash> restoredTrackHashes =
			(List<Hash>) my(BrickStateStore.class).readObjectFor(RejectedTracksKeeper.class);

		if (restoredTrackHashes == null) return;

		for (Hash restoredHash : restoredTrackHashes) {
			_rejectedTrackHashes.add(restoredHash);
		}
	}

	private void save() {
		List<Hash> trackHashesToPersist = new ArrayList<Hash>();
		for (Hash trackHashToPersist : _rejectedTrackHashes) {
			trackHashesToPersist.add(trackHashToPersist);
		}

		_store.writeObjectFor(RejectedTracksKeeper.class, trackHashesToPersist);
	}

}
