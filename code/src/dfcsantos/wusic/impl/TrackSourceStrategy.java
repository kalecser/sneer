package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

abstract class TrackSourceStrategy {

	private Playlist _playlist;

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;
	
	
	TrackSourceStrategy() {
		_refToAvoidGc = my(Timer.class).wakeUpEvery(5000, new Runnable() { @Override public void run() {
			disposeRejectedTracks();
		}});

		initPlaylist();
	}

	
	void initPlaylist() {
		initPlaylist(tracksFolder());
	}

	
	void initPlaylist(File tracksFolder) {
		_playlist = createPlaylist(tracksFolder);
	}


	void disposeRejectedTracks() {
		for (Track victim : my(RejectedTracksKeeper.class).rejected())
			if (!victim.file().delete())
				 my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to delete track", "Unable to delete track: " + victim.file(), 15000);
	}

	
	Track nextTrack() {
		return _playlist.nextTrack();
	}

	
	void markAsRejected(Track track) {
		my(RejectedTracksKeeper.class).reject(track);
	}

	
	void noWay(Track trackToDispose) {
		//Implement Create event to notify listeners of track rejection (musical taste matcher, for example).
		markAsRejected(trackToDispose);	
	}

	
	abstract Playlist createPlaylist(File tracksFolder);

	
	abstract File tracksFolder();

}
