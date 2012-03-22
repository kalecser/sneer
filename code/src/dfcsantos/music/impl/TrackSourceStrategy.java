package dfcsantos.music.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basis.lang.Closure;
import basis.lang.Functor;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.execution.playlist.Playlist;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

abstract class TrackSourceStrategy {

	private static final Track[] TRACK_ARRAY = new Track[]{};

	private Register<Playlist> _playlist = my(Signals.class).newRegister(null);

	private final List<Track> _tracksToKill = Collections.synchronizedList(new ArrayList<Track>());
	private WeakContract killTimer;


	TrackSourceStrategy() {
		initPlaylist();
	}


	void initPlaylist() {
		initPlaylist(tracksFolder());
	}


	void initPlaylist(File tracksFolder) {
		_playlist.setter().consume(createPlaylist(tracksFolder));
	}


	void killTracks() {
		for (Track victim : _tracksToKill.toArray(TRACK_ARRAY)) {
			if (!victim.file().exists()) throw new IllegalStateException();
			if (victim.file().delete())
				onTrackKilled(victim);
		}
	}


	synchronized
	protected void onTrackKilled(Track victim) {
		my(Logger.class).log("Track deleted: ", victim.file());
		_tracksToKill.remove(victim);
		controlKillTimer();
	}


	Signal<Integer> numberOfTracks() {
		return my(Signals.class).adapt(playlist(), new Functor<Playlist, Integer>() { @Override public Integer evaluate(Playlist playlist) throws RuntimeException {
			return playlist.numberOfTracks();
		}}); 
	}


	Track nextTrack() {
		killTracks();
		return playlist().currentValue().nextTrack();
	}


	void meh(final Track rejected) {
		Hash hash = deleteTrack(rejected);
		my(RejectedTracksKeeper.class).weakReject(hash);
	}

	void noWay(final Track rejected) {
		Hash hash = deleteTrack(rejected);
		my(RejectedTracksKeeper.class).strongReject(hash);
	}

	
	boolean isMarkedForDisposal(Track suspect) {
		return _tracksToKill.contains(suspect);
	}


	synchronized
	void condemn(Track victim) {
		my(Logger.class).log("Track marked for disposal: ", victim.file());
		_tracksToKill.add(victim);
		controlKillTimer();
	}


	private void controlKillTimer() {
		if (killTimer == null && !_tracksToKill.isEmpty())
			startKillTimer();
		if (killTimer != null && _tracksToKill.isEmpty())
			stopKillTimer();
	}


	private void startKillTimer() {
		killTimer = my(Timer.class).wakeUpEvery(5000, new Closure() { @Override public void run() {
			killTracks();
		}});
	}


	private void stopKillTimer() {
		killTimer.dispose();
		killTimer = null;
	}
	
	
	abstract Playlist createPlaylist(File tracksFolder);

	abstract File tracksFolder();

	private Signal<Playlist> playlist() {
		return _playlist.output();
	}

	
	private Hash deleteTrack(final Track rejected) {
		my(Logger.class).log("Rejecting track: ", rejected.file());
		String path = rejected.file().getAbsolutePath();
		Hash hash = my(FileMap.class).getHash(path);
		
		if (hash != null) {
			my(FileMap.class).remove(path);
		}
		
		condemn(rejected);
		
		return hash;
	}
}
