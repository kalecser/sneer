package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.events.EventSource;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.player.TrackContract;
import dfcsantos.tracks.player.TrackPlayer;

public class DJ implements Consumer<Track> {

	@SuppressWarnings("unused") private final WeakContract _toAvoidGc;
	private final Runnable _toCallWhenDonePlayingATrack;

	private TrackContract _currentTrackContract;

	
	DJ(EventSource<Track> trackToPlay, Runnable toCallWhenDonePlayingATrack) {
		_toCallWhenDonePlayingATrack = toCallWhenDonePlayingATrack;
		_toAvoidGc = trackToPlay.addReceiver(this);
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
	}


	void pauseResume() {
		if (_currentTrackContract == null) return;
		_currentTrackContract.pauseResume();
	}

	
	private void play(final Track track) {
		if (track == null) return;
		_currentTrackContract = my(TrackPlayer.class).startPlaying(track, _toCallWhenDonePlayingATrack);
	}

}
