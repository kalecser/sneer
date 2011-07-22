package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.exchange.TrackExchange;
import dfcsantos.tracks.exchange.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.exchange.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

	private final Register<OperatingMode> _currentOperatingMode = my(Signals.class).newRegister(OperatingMode.OWN);

	private TrackSourceStrategy _trackSource = OwnTracks.INSTANCE;
	private final Register<Track> _trackToPlay = my(Signals.class).newRegister(null);
	private Track _lastPlayedTrack;

	private final DJ _dj = new DJ(_trackToPlay.output(), new Closure() { @Override public void run() { skip(); } });

	private Register<Boolean> _isDownloadActive = my(Signals.class).newRegister(false);  

	@SuppressWarnings("unused") private final WeakContract _isDownloadActiveConsumerCtr;

	@SuppressWarnings("unused") private final WeakContract _operatingModeConsumerCtr;

	WusicImpl() {
		restore();

		my(TrackExchange.class).setOnOffSwitch(isTrackExchangeActive());

		_isDownloadActiveConsumerCtr = isTrackExchangeActive().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean notUsed) {
			save();
		}});

		_operatingModeConsumerCtr = operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode mode) {
			reset();
			_trackSource = (mode.equals(OperatingMode.OWN)) ? OwnTracks.INSTANCE : PeerTracks.INSTANCE;
		}});
	}

	private void restore() {
		Object[] restoredState = Store.restore();
		if (restoredState == null) return;

		_isDownloadActive.setter().consume((Boolean) restoredState[0]);

		if(restoredState.length > 2) {
			volumePercent((Integer)restoredState[2]);
		}
	}

	private void save() {
		Store.save(isTrackExchangeActive().currentValue(), null, volumePercent().currentValue()); // "null" was used by downloadAllowance that is no longer necessary
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
		my(TracksFolderKeeper.class).setPlayingFolder(playingFolder);
		skip();
	}

	@Override
	public Signal<File> sharedTracksFolder() {
		return my(TracksFolderKeeper.class).sharedTracksFolder();
	}

	@Override
	public void setSharedTracksFolder(File sharedTracksFolder) {
		my(TracksFolderKeeper.class).setSharedTracksFolder(sharedTracksFolder);
	}

	@Override
	public void setShuffle(boolean shuffle) {
		((OwnTracks)_trackSource).setShuffle(shuffle);
	}

	@Override
	public void start() {
		skip();
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
	public void deleteTrack() {
		final Track currentTrack = playingTrack().currentValue();
		if (currentTrack == null) return;

		_lastPlayedTrack = null;
		_trackSource.deleteTrack(currentTrack);
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
	public Signal<Boolean> isTrackExchangeActive() {
		return _isDownloadActive.output();
	}

	@Override
	public Consumer<Boolean> trackExchangeActivator() {
		return _isDownloadActive.setter();
	}

	@Override
	public SetSignal<Download> activeDownloads() {
		return my(TrackDownloader.class).runningDownloads();
	}

	@Override
	public void volumePercent(int level) {
		_dj.volumePercent(level);
		save();
	}

	@Override
	public Signal<Integer> volumePercent() {
		return _dj.volumePercent();
	}
}


