package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Closure;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.execution.playlist.Playlist;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

abstract class TrackSourceStrategy {

	private Playlist _playlist;

	private final List<Track> _tracksToDispose = Collections.synchronizedList(new ArrayList<Track>());

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;


	TrackSourceStrategy() {
		_refToAvoidGc = my(Timer.class).wakeUpEvery(5000, new Closure() { @Override public void run() {
			disposePendingTracks();
		}});

		initPlaylist();
	}


	void initPlaylist() {
		initPlaylist(tracksFolder());
	}


	void initPlaylist(File tracksFolder) {
		_playlist = createPlaylist(tracksFolder);
	}


	void disposePendingTracks() {
		List<Track> copy = new ArrayList<Track>(_tracksToDispose);
		for (Track victim : copy) {
			if (victim.file().delete())
				_tracksToDispose.remove(victim);
		}
	}


	Track nextTrack() {
		Track nextTrack = _playlist.nextTrack();
		if (_tracksToDispose.contains(nextTrack))
			return null;

		return nextTrack;
	}

	
	void noWay(final Track rejected) {
		Sneer1024 hash = my(FileMap.class).getHash(rejected.file());
		my(FileMap.class).remove(rejected.file());
		my(RejectedTracksKeeper.class).reject(hash);
		markForDisposal(rejected);	
	}


	void markForDisposal(Track trackToDispose) {
		_tracksToDispose.add(trackToDispose);
	}


	abstract Playlist createPlaylist(File tracksFolder);


	abstract File tracksFolder();

}
