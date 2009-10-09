package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

	private static final Format TIME_FORMATTER = new SimpleDateFormat("mm:ss");

	private Register<OperatingMode> _currentOperatingMode = my(Signals.class).newRegister(OperatingMode.OWN); 

	private TrackSourceStrategy _trackSource = OwnTracks.INSTANCE;

	private final Register<Track> _trackToPlay = my(Signals.class).newRegister(null);

	private final DJ _dj = new DJ(_trackToPlay.output(), new Runnable() { @Override public void run() {
		skip();
	}});

	@SuppressWarnings("unused") private WeakContract toAvoidGC;

	{
		toAvoidGC = operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode mode) {
			_trackSource = (mode == OperatingMode.OWN) ? OwnTracks.INSTANCE : PeerTracks.INSTANCE;
			skip();
		}});
	}

	@Override
	public void setOperatingMode(OperatingMode mode) {
		_currentOperatingMode.setter().consume(mode);
	}


	@Override
	public Signal<OperatingMode> operatingMode() {
		return _currentOperatingMode.output();
	}


	@Override
	public void setOwnTracksFolder(File ownTracksFolder) {
		my(TracksFolderKeeper.class).setOwnTracksFolder(ownTracksFolder);
		skip();
	}


	@Override
	public void setPeerTracksFolder(File peerTracksFolder) {
		my(TracksFolderKeeper.class).setPeerTracksFolder(peerTracksFolder);
		skip();
	}


	@Override
	public void setShuffleMode(boolean shuffle) {
		_trackSource.setShuffleMode(shuffle);
		skip();
	}


	@Override
	public void start() {
		skip();
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
	public void skip() {
		play(_trackSource.nextTrack());
	}


	private void play(final Track track) {
		_trackToPlay.setter().consume(track);
	}


	@Override
	public void stop() {
		play(null);
	}


	@Override
	public void meToo() {
		((PeerTracks)_trackSource).meToo(_trackToPlay.output().currentValue());
	}

	
	@Override
	public void noWay() {
		Track currentTrack = currentTrack();
		if (currentTrack == null) return;

		skip();
		((PeerTracks)_trackSource).noWay(currentTrack);
	}


	@Override
	public Signal<String> numberOfTracksFetchedFromPeers() {
		return my(Signals.class).adapt(my(TrackClient.class).numberOfTracksFetchedFromPeers(), new Functor<Integer, String>() { @Override public String evaluate(Integer numberOfTracks) {
			return (numberOfTracks == 0) ? "<No tracks>" : "<" + numberOfTracks + " tracks>";
		}});
	}


	@Override
	public Signal<Boolean> isPlaying() {
		return _dj.isPlaying();
	}

}
