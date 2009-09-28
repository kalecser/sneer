package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;
import dfcsantos.wusic.Track;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

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
	public Signal<String> trackPlayingName() {
		return my(Signals.class).adapt(_trackToPlay.output(), new Functor<Track, String>() { @Override public String evaluate(Track track) {
			return track == null ? "<No track to play>" : track.name();
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
	public void setShuffleMode(boolean shuffle) {
		_trackSource.setShuffleMode(shuffle);
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
