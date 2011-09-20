package dfcsantos.tracks.storage.rejected.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.cpu.crypto.Hash;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

class RejectedTracksKeeperImpl implements RejectedTracksKeeper {

	private final List<Hash> strongRejectedTrackHashes = Collections.synchronizedList(new ArrayList<Hash>());
	private final List<Hash> weakRejectedTrackHashes = Collections.synchronizedList(new ArrayList<Hash>());


	@Override
	public void strongReject(Hash hash) {
		strongRejectedTrackHashes.add(hash);
	}


	@Override
	public void weakReject(Hash hash) {
		weakRejectedTrackHashes.add(hash);
	}

	@Override
	public boolean isWeakRejected(Hash hash) {
		return strongRejectedTrackHashes.contains(hash);
	}

	
	@Override
	public boolean isRejected(Hash hash) {
		if (strongRejectedTrackHashes.contains(hash)) return true;
		if (weakRejectedTrackHashes.contains(hash)) return true;
		return false;
	}

}
