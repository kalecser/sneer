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

	private final Runnable _toCallWhenDonePlayingATrack;

	private TrackContract _currentTrackContract;
	private Register<Integer> _trackElapsedTime = my(Signals.class).newRegister(0);
	private Register<Boolean> _isPlaying = my(Signals.class).newRegister(false);

	@SuppressWarnings("unused") private final WeakContract _djContract;
	@SuppressWarnings("unused") private final WeakContract _timerContract;

	DJ(EventSource<Track> trackToPlay, Runnable toCallWhenDonePlayingATrack) {
		_toCallWhenDonePlayingATrack = toCallWhenDonePlayingATrack;
		_djContract = trackToPlay.addReceiver(this);

		_timerContract = my(Timer.class).wakeUpEvery(500, new Runnable() { @Override public void run() {
			refreshIsPlaying();
			if (_currentTrackContract != null)
				_trackElapsedTime.setter().consume(_currentTrackContract.trackElapsedTime());
		}});
	}


	private void refreshIsPlaying() {
		_isPlaying.setter().consume((_currentTrackContract == null)
			? false
			: !_currentTrackContract.isPaused().currentValue()
		);
	}


	@Override
	synchronized
	public void consume(Track track) {
		stop();
		if (track == null) return;
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
		_currentTrackContract = my(TrackPlayer.class).startPlaying(track, _toCallWhenDonePlayingATrack);
	}


	Signal<Integer> trackElapsedTime() {
		return _trackElapsedTime.output(); 
	}

	Signal<Boolean> isPlaying() {
		return _isPlaying.output();
	}

}
