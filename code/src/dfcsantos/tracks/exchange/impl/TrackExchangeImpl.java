package dfcsantos.tracks.exchange.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.gates.logic.LogicGates;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.exchange.TrackExchange;
import dfcsantos.tracks.exchange.downloads.downloader.TrackDownloader;
import dfcsantos.tracks.exchange.endorsements.TrackEndorser;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TrackExchangeImpl implements TrackExchange {

	private boolean _isInitialized;

	private File _currentTracksFolder;
	private File _newTracksFolder;

	private Register<Boolean> _isMapping = my(Signals.class).newRegister(false);

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;
	@SuppressWarnings("unused") private WeakContract _toAvoidGC2;

	{
		_toAvoidGC = my(TracksFolderKeeper.class).sharedTracksFolder().addReceiver(new Consumer<File>() { @Override public void consume(File newSharedTracksFolder) {
			_newTracksFolder = newSharedTracksFolder;
			updateMapping();
		}});
	}

	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		if (_isInitialized) throw new IllegalStateException("TrackSharing already initialized");

		Signal<Boolean> isNotMapping = my(Signals.class).adapt(_isMapping.output(), new Functor<Boolean, Boolean>() { @Override public Boolean evaluate(Boolean isMapping) { return !isMapping; }});
		Signal<Boolean> isTrackExchangeActive = my(LogicGates.class).and(onOffSwitch, isNotMapping);

		my(TrackDownloader.class).setOnOffSwitch(isTrackExchangeActive);
		my(TrackEndorser.class).setOnOffSwitch(isTrackExchangeActive);
	}

	@Override
	public void setDownloadAllowance(Signal<Integer> downloadAllowance) {
		my(TrackDownloader.class).setDownloadAllowance(downloadAllowance);
	}

	synchronized
	private void updateMapping() {
		stopCurrentMappingIfNecessary();
		cleanOldMappingIfNecessary();
		startNewMappingIfNecessary();
	}

	private void stopCurrentMappingIfNecessary() {
		if (!shouldStop()) return;

		my(FileMapper.class).stopFolderMapping(_currentTracksFolder);
	}

	private boolean shouldStop() {
		return _isMapping.output().currentValue(); 
	}

	private void cleanOldMappingIfNecessary() {
		if (!shouldClean()) return;

		my(Threads.class).startDaemon("Cleaning Track Mapping", new Closure() { @Override public void run() {
			my(FileMap.class).remove(_currentTracksFolder);
			_currentTracksFolder = null;
		}});
	}

	private boolean shouldClean() {
		return _currentTracksFolder != null;
	}

	private void startNewMappingIfNecessary() {
		if (!shouldStart()) return;

		_isMapping.setter().consume(true);

		_currentTracksFolder = _newTracksFolder;
		_newTracksFolder = null;

		my(Threads.class).startDaemon("Track Mapping", new Closure() { @Override public void run() {
			mapSharedTracksFolder(_currentTracksFolder);
		}});
	}

	private boolean shouldStart() {
		return _newTracksFolder != null;
	}

	private void mapSharedTracksFolder(File newSharedTracksFolder) {
		try {
			my(FileMapper.class).mapFolder(newSharedTracksFolder, "mp3");
			_isMapping.setter().consume(false);
		} catch (MappingStopped ignored) {
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error while reading tracks.", "", e);
		}
	}

}
