package dfcsantos.music.impl;

import static basis.environments.Environments.my;

import java.io.File;

import basis.lang.Closure;
import basis.lang.Consumer;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import dfcsantos.music.Music;
import dfcsantos.music.store.MusicStore;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.exchange.TrackExchange;
import dfcsantos.tracks.exchange.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.exchange.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

public class MusicImpl implements Music {

	private final Register<OperatingMode> _currentOperatingMode = my(Signals.class).newRegister(OperatingMode.OWN);

	private TrackSourceStrategy _trackSource = OwnTracks.INSTANCE;
	private final Register<Track> _trackToPlay = my(Signals.class).newRegister(null);
	private Track _lastPlayedTrack;

	private final DJ _dj = new DJ(_trackToPlay.output(), new Closure() { @Override public void run() { skip(); } });

	@SuppressWarnings("unused") private final WeakContract refToAvoidGc;
	@SuppressWarnings("unused") private final WeakContract refToAvoidGc2;
	@SuppressWarnings("unused") private final WeakContract refToAvoidGc3;

	{
		my(TrackExchange.class).setOnOffSwitch(isTrackExchangeActive().output());

		refToAvoidGc = my(MusicStore.class).volumePercent().output().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer percent) {
			_dj.volumePercent(percent);
		}});
		refToAvoidGc2 = my(MusicStore.class).shuffle().output().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean onOff) {
			((OwnTracks)_trackSource).setShuffle(onOff);
		}});
		refToAvoidGc3 = operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode mode) {
			reset();
			_trackSource = (mode.equals(OperatingMode.OWN)) ? OwnTracks.INSTANCE : PeerTracks.INSTANCE;
		}});
	}

	private void reset() {
		_lastPlayedTrack = null;
		stop();
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
	public File playingFolder() {
		return _trackSource.tracksFolder();
	}

	@Override
	public void setPlayingFolder(File playingFolder) {
		my(TracksFolderKeeper.class).setPlayingFolder(playingFolder.getAbsolutePath());
		skip();
	}

	@Override
	public Signal<File> tracksFolder() {
		return my(TracksFolderKeeper.class).tracksFolder();
	}

	@Override
	public void setTracksFolder(File tracksFolder) {
		my(TracksFolderKeeper.class).setTracksFolder(tracksFolder.getAbsolutePath());
	}


	@Override
	public void pauseResume() {
		if (playingTrack().currentValue() == null)
			play();
		else
			_dj.pauseResume();
	}

	@Override
	public void skip() {
		Track nextTrack = _trackSource.nextTrack();
		if (nextTrack != null && nextTrack.equals(_lastPlayedTrack)) stop(); // one-track scenario
		play(nextTrack);
	}

	private void play() {
		if (_lastPlayedTrack == null)
			skip();
		else
			play(_lastPlayedTrack);
	}

	private void play(final Track track) {
		_trackToPlay.setter().consume(track);
		if (track != null) _lastPlayedTrack = track;
	}

	@Override
	public void stop() {
		play(null);
	}

	@Override
	public void meToo() {
		_lastPlayedTrack = null;
		((PeerTracks)_trackSource).meToo(playingTrack().currentValue());
	}
	
	
	@Override
	public void meh() {
		final Track currentTrack = playingTrack().currentValue();
		if (currentTrack == null) return;

		_lastPlayedTrack = null;
		_trackSource.meh(currentTrack);
		skip();
	}
	
	
	@Override
	public void noWay() { 
		final Track currentTrack = playingTrack().currentValue();
		if (currentTrack == null) return;

		_lastPlayedTrack = null;
		_trackSource.noWay(currentTrack);
		skip();
	}

	@Override
	public Signal<Boolean> isPlaying() {
		return _dj.isPlaying();
	}

	@Override
	public Signal<Track> playingTrack() {
		return _trackToPlay.output();
	}

	@Override
	public Signal<Integer> playingTrackTime() {
		return _dj.trackElapsedTime();
	}

	@Override
	public Signal<Integer> numberOfOwnTracks() {
		return _trackSource.numberOfTracks();
	}

	@Override
	public Signal<Integer> numberOfPeerTracks() {
		return my(TrackDownloadCounter.class).count();
	}

	@Override
	public Register<Boolean> isTrackExchangeActive() {
		return my(MusicStore.class).isExchangeTracksOn();
	}

	@Override
	public SetSignal<Download> activeDownloads() {
		return my(TrackDownloader.class).runningDownloads();
	}

	@Override
	public Register<Integer> volumePercent() {
		return my(MusicStore.class).volumePercent();
	}

	@Override
	public Register<Boolean> shuffle() {
		return my(MusicStore.class).shuffle();
	}

}


