package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.playlist.Playlist;

abstract class TrackSourceStrategy {

	private Playlist _playlist;

	private final List<Track> _tracksToDispose = new ArrayList<Track>();

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
		for (Track victim : _tracksToDispose)
			if (!victim.file().delete())
				 my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to delete track", "Unable to delete track: " + victim.file(), 15000);
	}

	
	Track nextTrack() {
		return _playlist.nextTrack();
	}

	
	void markForDisposal(Track track) {
		track.ignore();
		_tracksToDispose.add(track);
	}

	
	void noWay(Track trackToDispose) {
		//Implement Create event to notify listeners of track rejection (musical taste matcher, for example).
		markForDisposal(trackToDispose);	
	}

	
	abstract Playlist createPlaylist(File tracksFolder);

	
	abstract File tracksFolder();

}
