package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;
import dfcsantos.tracks.player.TrackContract;
import dfcsantos.tracks.player.TrackPlayer;
import dfcsantos.wusic.Track;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

	private final Register<Track> _trackPlaying = my(Signals.class).newRegister(null);
	private TrackSourceStrategy _trackSource = OwnTracks.INSTANCE;
	private TrackContract _currentTrackContract;

	@Override
	public void start() {
		playNextTrack();
	}

	@Override
	public void setMyTracksFolder(File ownTracksFolder) {
		my(OwnTracksFolderKeeper.class).setOwnTracksFolder(ownTracksFolder);
		stop();
	}

	@Override
	public void pauseResume(){
		_currentTrackContract.pauseResume();
	}

	@Override
	public void skip() {
		stop();
		playNextTrack();
	}

	private void playNextTrack() {
		Track trackToPlay = _trackSource.nextTrack();
		if (trackToPlay == null) return;
		play(trackToPlay);
	}

	private void play(final Track track) {
		FileInputStream stream = openFileStream(track);
		if (stream == null) return;
		_trackPlaying.setter().consume(track);
		_currentTrackContract = my(TrackPlayer.class).startPlaying(stream, new Runnable() { @Override public void run() {
			playNextTrack();
		}});
	}

	private FileInputStream openFileStream(Track trackToPlay) {
		try {
			return new FileInputStream(trackToPlay.file());
		} catch (FileNotFoundException e) {
			my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to find file " + trackToPlay.file() , "File might have been deleted manually.", 15000);
			return null;
		}
	}

	@Override
	public void stop() {
		if (_currentTrackContract != null)
			_currentTrackContract.dispose();
	}

	@Override
	public Signal<String> trackPlayingName() {
		return my(Signals.class).adapt(_trackPlaying.output(), new Functor<Track, String>() { @Override public String evaluate(Track track) {
		return track == null ? "<No track playing>" : track.name();
		}});
	}

	@Override
	public void chooseTrackSource(TrackSource source) {
		_trackSource = source == TrackSource.OWN_TRACKS
			? OwnTracks.INSTANCE
			: PeerTracks.INSTANCE;
		skip();
	}

	@Override
	public void meToo() {
		((PeerTracks)_trackSource).meToo();
	}

	@Override
	public void noWay() {
		Track currentTrack = _trackPlaying.output().currentValue();
		skip();
		_trackSource.noWay(currentTrack);
	}

}
