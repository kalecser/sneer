package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.player.TrackContract;
import dfcsantos.tracks.player.TrackPlayer;

public class DJ implements Consumer<Track> {

	@SuppressWarnings("unused") private final WeakContract _toAvoidGc;
	@SuppressWarnings("unused") private final WeakContract _timerContract;

	private final Runnable _toCallWhenDonePlayingATrack;

	private TrackContract _currentTrackContract;

	private Register<Integer> _trackElapsedTime = my(Signals.class).newRegister(0);

	DJ(EventSource<Track> trackToPlay, Runnable toCallWhenDonePlayingATrack) {
		_toCallWhenDonePlayingATrack = toCallWhenDonePlayingATrack;
		_toAvoidGc = trackToPlay.addReceiver(this);

		_timerContract = my(Timer.class).wakeUpEvery(1000, new Runnable() { @Override public void run() {
			if (_currentTrackContract != null)
				_trackElapsedTime.setter().consume(_currentTrackContract.trackElapsedTime());
		}});
	}


	@Override
	synchronized
	public void consume(Track track) {
		stop();
		play(track);
	}


	private void stop() {
		if (_currentTrackContract == null)
			return;
		_currentTrackContract.dispose();
		_currentTrackContract = null;
		_trackElapsedTime.setter().consume(0);
	}


	void pauseResume() {
		if (_currentTrackContract == null) return;
		_currentTrackContract.pauseResume();
	}


	private void play(final Track track) {
		if (track == null) return;
		_currentTrackContract = my(TrackPlayer.class).startPlaying(track, _toCallWhenDonePlayingATrack);
	}


	Signal<Integer> trackElapsedTime() {
		return _trackElapsedTime.output(); 
	}

}
