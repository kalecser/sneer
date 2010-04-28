package dfcsantos.tracks.endorsements.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.endorsements.client.TrackClient;
import dfcsantos.tracks.endorsements.client.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.endorsements.server.TrackEndorser;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TrackClientImpl implements TrackClient {

	private boolean _isOn;
	private Latch _isHandlingFileMap = my(Latches.class).produce();;

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();
	private final Register<Boolean> _trackExchangeActivator = my(Signals.class).newRegister(false);

	private File _tracksFolder;
	private File _newTracksFolder;

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;
	@SuppressWarnings("unused") private WeakContract _toAvoidGC2;

	{
		my(TrackDownloader.class).setOnOffSwitch(_trackExchangeActivator.output());
		my(TrackEndorser.class).setOnOffSwitch(_trackExchangeActivator.output());

		_toAvoidGC = my(TracksFolderKeeper.class).sharedTracksFolder().addReceiver(new Consumer<File>() { @Override public void consume(File newSharedTracksFolder) {
			_newTracksFolder = newSharedTracksFolder;
			updateMapping();
		}});
	}

	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		_onOffSwitch.set(onOffSwitch);

		_toAvoidGC2 = onOffSwitch.addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isOn) {
			_isOn = isOn;

			if (_isHandlingFileMap.isOpen())
				_trackExchangeActivator.setter().consume(isOn);
		}});
	}

	@Override
	public void setTrackDownloadAllowance(Signal<Integer> downloadAllowance) {
		my(TrackDownloader.class).setTrackDownloadAllowance(downloadAllowance);
	}

	synchronized
	private void updateMapping() {
		stopOldMappingIfNecessary();
		startNewMappingIfNecessary();
	}

	private void stopOldMappingIfNecessary() {
		if (shouldStop()) {
			_isHandlingFileMap = my(Latches.class).produce();
			_trackExchangeActivator.setter().consume(false);			
			my(FileMapper.class).stopFolderMapping(_tracksFolder);
			_tracksFolder = null;
		}
	}

	private boolean shouldStop() {
		return _tracksFolder != null;
	}

	private void startNewMappingIfNecessary() {
		if (!shouldStart()) return;

		_tracksFolder = _newTracksFolder;
		_newTracksFolder = null;

		my(Threads.class).startDaemon("Track Mapping", new Closure() { @Override public void run() {
			mapSharedTracksFolder(_tracksFolder);
		}});
	}
	
	private boolean shouldStart() {
		return _newTracksFolder != null;
	}

	private void mapSharedTracksFolder(File newSharedTracksFolder) {
		try {
			my(FileMapper.class).mapFolder(newSharedTracksFolder, "mp3");
			_isHandlingFileMap.open();
			_trackExchangeActivator.setter().consume(_isOn);
		} catch (MappingStopped ignored) {
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error while reading tracks.", "", e);
		}
	}

}
