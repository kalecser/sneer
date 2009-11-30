package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.bricks.statestore.BrickStateStore;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.PickyConsumer;
import sneer.foundation.lang.exceptions.Refusal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

	private static final BrickStateStore _store = my(BrickStateStore.class);

	private static final Format TIME_FORMATTER = new SimpleDateFormat("mm:ss");

	private final Register<OperatingMode> _currentOperatingMode = my(Signals.class).newRegister(OperatingMode.OWN);

	private final Register<Track> _trackToPlay = my(Signals.class).newRegister(null);

	private TrackSourceStrategy _trackSource = OwnTracks.INSTANCE;

	private Track _lastPlayedTrack;

	private final DJ _dj = new DJ(_trackToPlay.output(), new Runnable() { @Override public void run() { skip(); } } );

	private Register<Boolean> _isTracksDownloadEnabled = my(Signals.class).newRegister(false);
	private final Register<Integer> _tracksDownloadAllowance = my(Signals.class).newRegister(DEFAULT_TRACKS_DOWNLOAD_ALLOWANCE);  

	@SuppressWarnings("unused") private WeakContract _downloadAllowanceConsumerContract;
	@SuppressWarnings("unused") private WeakContract _isDownloadEnabledConsumerContract;

	@SuppressWarnings("unused") private final WeakContract _operatingModeConsumerContract;



	WusicImpl() {
		takeCareOfPersistence();

		_operatingModeConsumerContract = operatingMode().addReceiver(new Consumer<OperatingMode>() { @Override public void consume(OperatingMode mode) {
			reset();
			_trackSource = (mode.equals(OperatingMode.OWN)) ? OwnTracks.INSTANCE : PeerTracks.INSTANCE;
		}});
	}


	private void reset() {
		stop();
		_lastPlayedTrack = null;
	}


	@Override
	public void switchOperatingMode() {
		setOperatingMode(
			operatingMode().currentValue().equals(OperatingMode.OWN)
				? OperatingMode.PEERS
				: OperatingMode.OWN
		);
	}


	private void setOperatingMode(OperatingMode mode) {
		_currentOperatingMode.setter().consume(mode);
	}


	@Override
	public Signal<OperatingMode> operatingMode() {
		return _currentOperatingMode.output();
	}


	@Override
	public void setPlayingFolder(File playingFolder) {
		my(TracksFolderKeeper.class).setPlayingFolder(playingFolder);
		skip();
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
	public Signal<String> playingTrackName() {
		return my(Signals.class).adapt(_trackToPlay.output(), new Functor<Track, String>() { @Override public String evaluate(Track track) {
			return (track == null)
					? "<No track to play>"
					: (track.name().length() >= 54) ? track.name().substring(0, 51).concat("...") : track.name();
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
			play();
		else
			_dj.pauseResume();
	}


	private Track currentTrack() {
		return _trackToPlay.output().currentValue();
	}


	@Override
	public void back() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}


	@Override
	public void skip() {
		Track nextTrack = _trackSource.nextTrack();
		if (nextTrack == null || nextTrack.equals(_lastPlayedTrack))
			stop();
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
		((PeerTracks)_trackSource).meToo(_trackToPlay.output().currentValue());
	}

	
	@Override
	public void noWay() {
		final Track currentTrack = currentTrack();
		if (currentTrack == null) return;

		_trackSource.noWay(currentTrack);
		skip();
	}


	@Override
	public Signal<String> numberOfPeerTracks() {
		return my(Signals.class).adapt(my(TrackClient.class).numberOfDownloadedTracks(), new Functor<Integer, String>() { @Override public String evaluate(Integer numberOfTracks) {
			return "Peer Tracks (" + numberOfTracks + ")";
		}});
	}


	@Override
	public Signal<Boolean> isPlaying() {
		return _dj.isPlaying();
	}

	@Override
	public Signal<Boolean> isTracksDownloadEnabled() {
		return _isTracksDownloadEnabled.output();
	}

	@Override
	public void enableTracksDownload() {
		_isTracksDownloadEnabled.setter().consume(true);
	}

	@Override
	public void disableTracksDownload() {
		_isTracksDownloadEnabled.setter().consume(false);
	}

	@Override
	public Signal<Integer> tracksDownloadAllowance() {
		return _tracksDownloadAllowance.output();
	}

	@Override
	public PickyConsumer<Integer> tracksDownloadAllowanceSetter() {
		return new PickyConsumer<Integer>() { @Override public void consume(Integer allowanceInMBs) throws Refusal {
			validateDownloadAllowance(allowanceInMBs);
			_tracksDownloadAllowance.setter().consume(allowanceInMBs);
		}};
	}

	private void validateDownloadAllowance(Integer allowanceInMBs) throws Refusal {
		if (allowanceInMBs == null || allowanceInMBs < 0) throw new Refusal("Invalid tracks' download allowance: it must be positive integer");
	}

	private void takeCareOfPersistence() {
		restore();

		_isDownloadEnabledConsumerContract =  isTracksDownloadEnabled().addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isTracksDownloadEnabled) {
			save(asListOfStrings(isTracksDownloadEnabled().currentValue(), tracksDownloadAllowance().currentValue()));			
		}});

		_downloadAllowanceConsumerContract = tracksDownloadAllowance().addReceiver(new Consumer<Integer>(){ @Override public void consume(Integer downloadAllowanceInMBs) {
			save(asListOfStrings(isTracksDownloadEnabled().currentValue(), downloadAllowanceInMBs));
		}});
	}

	private void restore() {
		List<String> restoredDownloadAllowanceState = (List<String>) _store.readObjectFor(Wusic.class, getClass().getClassLoader());
		if (restoredDownloadAllowanceState == null)
			return;

		if (Boolean.parseBoolean(restoredDownloadAllowanceState.get(0)))
			enableTracksDownload();
		else
			disableTracksDownload();

		final String allowanceValue = restoredDownloadAllowanceState.get(1);
		try {
			tracksDownloadAllowanceSetter().consume(Integer.parseInt(allowanceValue));
		} catch (Exception e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error trying to restore Wusic State", "Invalid value for tracks download allowance: " + allowanceValue, e, 20000);
		}
	}

	private List<String> asListOfStrings(boolean isTracksDownloadEnabled, int tracksDownloadAllowance) {
		return Arrays.asList(String.valueOf(isTracksDownloadEnabled), String.valueOf(tracksDownloadAllowance));
	}

	private void save(List<String> downloadAllowanceState) {
		_store.writeObjectFor(Wusic.class, downloadAllowanceState);
	}

}
