package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.crypto.Sneer1024;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

abstract class TrackSourceStrategy {

	private Playlist _playlist;

	private final List<Track> _tracksToDispose = Collections.synchronizedList(new ArrayList<Track>());

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;
	
	
	TrackSourceStrategy() {
		_refToAvoidGc = my(Timer.class).wakeUpEvery(5000, new Runnable() { @Override public void run() {
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
		Track nextTrack = null;
		do {
			nextTrack = _playlist.nextTrack();
		} while (_tracksToDispose.contains(nextTrack));

		return nextTrack; 
	}

	
	void noWay(final Track rejected) {
		//Implement Create event to notify listeners of track rejection (musical taste matcher, for example).
		my(Threads.class).startDaemon("Calculating Hash to Reject Track", new Runnable() { @Override public void run() {
			Sneer1024 hash = my(Tracks.class).calculateHashFor(rejected);
			my(RejectedTracksKeeper.class).reject(hash);
		}});
		markForDisposal(rejected);	
	}

	
	void markForDisposal(Track trackToDispose) {
		_tracksToDispose.add(trackToDispose);
	}

	
	abstract Playlist createPlaylist(File tracksFolder);

	
	abstract File tracksFolder();

}
