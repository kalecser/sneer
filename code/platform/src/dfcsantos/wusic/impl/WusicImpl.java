package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

	private static final Format TIME_FORMATTER = new SimpleDateFormat("mm:ss");

	private TrackSourceStrategy _trackSource = OwnTracks.INSTANCE;

	private final Register<Track> _trackToPlay = my(Signals.class).newRegister(null);

	private final DJ _dj = new DJ(_trackToPlay.output(), new Runnable() { @Override public void run() {
		skip();
	}});

	
	@Override
	public void start() {
		skip();
	}

	
	@Override
	public void setMyTracksFolder(File ownTracksFolder) {
		my(OwnTracksFolderKeeper.class).setOwnTracksFolder(ownTracksFolder);
		skip();
	}

	
	@Override
	public void skip() {
		play(_trackSource.nextTrack());
	}

	
	@Override
	public void pauseResume() {
		if (currentTrack() == null)
			skip();
		else
			_dj.pauseResume();
	}


	private Track currentTrack() {
		return _trackToPlay.output().currentValue();
	}

	
	@Override
	public void stop() {
		play(null);
	}

	
	private void play(final Track track) {
		_trackToPlay.setter().consume(track);
	}

	
	@Override
	public Signal<String> playingTrackName() {
		return my(Signals.class).adapt(_trackToPlay.output(), new Functor<Track, String>() { @Override public String evaluate(Track track) {
			return track == null ? "<No track to play>" : track.name();
		}});
	}

	@Override
	public Signal<String> playingTrackTime() {
		return my(Signals.class).adapt(_dj.trackElapsedTime(), new Functor<Integer, String>() { @Override public String evaluate(Integer timeElapsed) {
			return TIME_FORMATTER.format(new Date(timeElapsed));
		}});
	}

	@Override
	public void setOperatingMode(OperatingMode mode) {
		_trackSource = (mode == OperatingMode.OWN) ? OwnTracks.INSTANCE : PeerTracks.INSTANCE;
		skip();
	}

	
	@Override
	public void setShuffleMode(boolean shuffle) {
		_trackSource.setShuffleMode(shuffle);
		skip();
	}

	
	@Override
	public void meToo() {
		((PeerTracks)_trackSource).meToo();
	}

	
	@Override
	public void noWay() {
		Track currentTrack = currentTrack();
		if (currentTrack == null) return;

		skip();
		_trackSource.noWay(currentTrack);
	}

}
