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
import sneer.foundation.lang.PickyConsumer;
import sneer.foundation.lang.exceptions.Refusal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.endorsements.client.TrackClient;
import dfcsantos.tracks.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.endorsements.client.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.endorsements.server.TrackEndorser;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

	private final Register<OperatingMode> _currentOperatingMode = my(Signals.class).newRegister(OperatingMode.OWN);

	private TrackSourceStrategy _trackSource = OwnTracks.INSTANCE;
	private final Register<Track> _trackToPlay = my(Signals.class).newRegister(null);
	private Track _lastPlayedTrack;

	private final DJ _dj = new DJ(_trackToPlay.output(), new Closure() { @Override public void run() { skip(); } } );

	private Register<Boolean> _isDownloadActive = my(Signals.class).newRegister(false);
	private final Register<Integer> _downloadAllowance = my(Signals.class).newRegister(DEFAULT_TRACKS_DOWNLOAD_ALLOWANCE);  

	@SuppressWarnings("unused") private final WeakContract _downloadAllowanceConsumerCtr;
	@SuppressWarnings("unused") private final WeakContract _isDownloadActiveConsumerCtr;

	@SuppressWarnings("unused") private final WeakContract _operatingModeConsumerCtr;

	WusicImpl() {
		restore();

		my(TrackClient.class).setOnOffSwitch(isTrackDownloadActive());
		my(TrackClient.class).setTrackDownloadAllowance(trackDownloadAllowance());

		my(TrackEndorser.class).setOnOffSwitch(isTrackDownloadActive());

		_isDownloadActiveConsumerCtr = isTrackDownloadActive().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean notUsed) {
			save();
		}});

		_downloadAllowanceConsumerCtr = trackDownloadAllowance().addReceiver(new Consumer<Integer>(){ @Override public void consume(Integer notUsed) {
			save();
		}});

		_operatingModeConsumerCtr = operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode mode) {
			reset();
			_trackSource = (mode.equals(OperatingMode.OWN)) ? OwnTracks.INSTANCE : PeerTracks.INSTANCE;
		}});
	}

	private void restore() {
		Object[] restoredDownloadAllowanceState = Store.restore();
		if (restoredDownloadAllowanceState == null) return;

		_isDownloadActive.setter().consume((Boolean) restoredDownloadAllowanceState[0]);
		try {
			trackDownloadAllowanceSetter().consume((Integer) restoredDownloadAllowanceState[1]);
		} catch (Refusal e) {
			throw new IllegalStateException(e);
		}
	}

	private void save() {
		Store.save(isTrackDownloadActive().currentValue(), trackDownloadAllowance().currentValue());
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
	public Signal<Boolean> isTrackDownloadActive() {
		return _isDownloadActive.output();
	}

	@Override
	public Consumer<Boolean> trackDownloadActivator() {
		return _isDownloadActive.setter();
	}

	@Override
	public SetSignal<Download> activeTrackDownloads() {
		return my(TrackDownloader.class).runningDownloads();
	}

	@Override
	public Signal<Integer> trackDownloadAllowance() {
		return _downloadAllowance.output();
	}

	@Override
	public PickyConsumer<Integer> trackDownloadAllowanceSetter() {
		return new PickyConsumer<Integer>() { @Override public void consume(Integer allowanceInMBs) throws Refusal {
			validateDownloadAllowance(allowanceInMBs);
			_downloadAllowance.setter().consume(allowanceInMBs);
		}};
	}

	private void validateDownloadAllowance(Integer allowanceInMBs) throws Refusal {
		if (allowanceInMBs == null || allowanceInMBs < 0) throw new Refusal("Invalid tracks' download allowance: it must be positive integer");
	}

	@Override
	public void volumePercent(int level) {
		_dj.volumePercent(level);
	}

	@Override
	public int volumePercent() {
		return _dj.volumePercent();
	}
}


