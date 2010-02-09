package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.execution.player.TrackContract;
import dfcsantos.tracks.execution.player.TrackPlayer;

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

		_timerContract = my(Timer.class).wakeUpEvery(500, new Closure() { @Override public void run() {
			if (_currentTrackContract != null)
				_trackElapsedTime.setter().consume(_currentTrackContract.trackElapsedTime());
		}});
	}


	@Override
	synchronized
	public void consume(Track track) {
		stop();
		if (track == null) return;
		play(track);
	}


	private void stop() {
		setPlaying(false);
		_trackElapsedTime.setter().consume(0);
		
		if (_currentTrackContract == null)
			return;
		_currentTrackContract.dispose();
		_currentTrackContract = null;
	}


	void pauseResume() {
		setPlaying(!isPlaying().currentValue());
	}


	private void setPlaying(boolean isPlaying) {
		_isPlaying.setter().consume(isPlaying);
	}


	private void play(final Track track) {
		setPlaying(true);
		_currentTrackContract = my(TrackPlayer.class).startPlaying(track, isPlaying(), _toCallWhenDonePlayingATrack);
	}


	Signal<Integer> trackElapsedTime() {
		return _trackElapsedTime.output(); 
	}

	Signal<Boolean> isPlaying() {
		return _isPlaying.output();
	}

}
