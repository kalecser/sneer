package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

abstract class TrackSourceStrategy {

	private Playlist _playlist;

	private final List<Track> _tracksToDispose = new ArrayList<Track>();

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;
	
	
	TrackSourceStrategy() {
		_refToAvoidGc = my(Timer.class).wakeUpEvery(10000, new Runnable() { @Override public void run() {
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
		for (Iterator<Track> iterator = _tracksToDispose.iterator(); iterator.hasNext();) {
			Track victim = iterator.next();
			if (victim.file().delete())
				iterator.remove();
			else
				my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to delete track", "Unable to delete track: " + victim.file(), 15000);
		}
	}

	
	Track nextTrack() {
		return _playlist.nextTrack();
	}

	
	void noWay(Track rejected) {
		//Implement Create event to notify listeners of track rejection (musical taste matcher, for example).
		my(RejectedTracksKeeper.class).reject(rejected.hash());
		markForDisposal(rejected);	
	}

	
	void markForDisposal(Track trackToDispose) {
		_tracksToDispose.add(trackToDispose);
	}

	
	abstract Playlist createPlaylist(File tracksFolder);

	
	abstract File tracksFolder();

}
