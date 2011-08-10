package dfcsantos.tracks.storage.rejected.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.cpu.crypto.Hash;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

class RejectedTracksKeeperImpl implements RejectedTracksKeeper {

	private final List<Hash> rejectedTrackHashes = Collections.synchronizedList(new ArrayList<Hash>());


	@Override
	public void reject(Hash hash) {
		rejectedTrackHashes.add(hash);
	}

	@Override
	public boolean isRejected(Hash hash) {
		return rejectedTrackHashes.contains(hash);
	}

}
