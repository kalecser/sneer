package dfcsantos.tracks.sharing.endorsements.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.sharing.endorsements.client.TrackClient;
import dfcsantos.tracks.sharing.endorsements.client.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TrackClientImpl implements TrackClient {

	private static final FileMapper _fileMapper = my(FileMapper.class);

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();
	private final Register<Boolean> _downloadActivator = my(Signals.class).newRegister(false);

	private File _tracksFolder;
	private File _newTracksFolder;

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;
	@SuppressWarnings("unused") private WeakContract _toAvoidGC2;


	{
		my(TrackDownloader.class).setOnOffSwitch(_downloadActivator.output());

		_toAvoidGC = my(TracksFolderKeeper.class).sharedTracksFolder().addReceiver(new Consumer<File>() { @Override public void consume(File newSharedTracksFolder) {
			_newTracksFolder = newSharedTracksFolder;
			react();
		}});
	}

	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		_onOffSwitch.set(onOffSwitch);

		_toAvoidGC2 = onOffSwitch.addPulseReceiver(new Runnable() { @Override public void run() {
			react();
		}});
	}

	synchronized
	private void react() {
		stopOldMappingIfNecessary();
		startNewMappingIfNecessary();
	}

	private void stopOldMappingIfNecessary() {
		if (shouldStop()) {
			_downloadActivator.setter().consume(false);
			_fileMapper.stopFolderMapping(_tracksFolder);
			_tracksFolder = null;
		}
	}

	private boolean shouldStop() {
		if (_tracksFolder == null) return false;
		return !isOn();
	}

	private Boolean isOn() {
		if (!_onOffSwitch.isAlreadySet()) return false;
		return _onOffSwitch.get().currentValue();
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
		if (_newTracksFolder == null) return false;
		return isOn();
	}

	private void mapSharedTracksFolder(File newSharedTracksFolder) {
		try {
			_fileMapper.mapFolder(newSharedTracksFolder, "mp3");
			_downloadActivator.setter().consume(true);
		} catch (MappingStopped ignored) {
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error while reading tracks.", "", e);
		}
	}

}
