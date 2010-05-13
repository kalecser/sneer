package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.execution.playlist.Playlist;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

abstract class TrackSourceStrategy {

	private Register<Playlist> _playlist = my(Signals.class).newRegister(null);

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
		_playlist.setter().consume(createPlaylist(tracksFolder));
	}


	void disposePendingTracks() {
		List<Track> copy = new ArrayList<Track>(_tracksToDispose);
		for (Track victim : copy) {
			if (victim.file().delete()) {
				my(Logger.class).log("Track disposed: ", victim.file());
				_tracksToDispose.remove(victim);
			}
		}
	}


	Signal<Integer> numberOfTracks() {
		return my(Signals.class).adapt(playlist(), new Functor<Playlist, Integer>() { @Override public Integer evaluate(Playlist playlist) throws RuntimeException {
			return playlist.numberOfTracks();
		}}); 
	}


	Track nextTrack() {
		disposePendingTracks();
		return playlist().currentValue().nextTrack();
	}


	void deleteTrack(final Track rejected) {
		my(Logger.class).log("Rejecting track: ", rejected.file());
		Hash hash = my(FileMap.class).getHash(rejected.file());
		my(FileMap.class).remove(rejected.file());
		my(RejectedTracksKeeper.class).reject(hash);
		markForDisposal(rejected);
	}


	boolean isMarkedForDisposal(Track suspect) {
		return _tracksToDispose.contains(suspect);
	}


	void markForDisposal(Track trackToDispose) {
		my(Logger.class).log("Track marked for disposal: ", trackToDispose.file());
		_tracksToDispose.add(trackToDispose);
	}


	abstract Playlist createPlaylist(File tracksFolder);

	abstract File tracksFolder();

	private Signal<Playlist> playlist() {
		return _playlist.output();
	}

}
