package dfcsantos.tracks.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.downloads.TrackDownloader;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;

class TrackClientImpl implements TrackClient {

	private static final FileMapper _fileMapper = my(FileMapper.class);

	private File _lastSharedTracksFolder;

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	private Latch _busy;

	{
		_toAvoidGC = my(TracksFolderKeeper.class).sharedTracksFolder().addReceiver(new Consumer<File>() { @Override public void consume(File newSharedTracksFolder) {
			consumeNewSharedFolder(newSharedTracksFolder);
		}});
	}

	private void consumeNewSharedFolder(final File newSharedTracksFolder) {
		if (_lastSharedTracksFolder != null)
			_fileMapper.stopFolderMapping(_lastSharedTracksFolder);
		_lastSharedTracksFolder = newSharedTracksFolder;

		if (_busy != null) _busy.waitTillOpen();
		_busy = my(Latches.class).produce();
		my(Threads.class).startDaemon("Track Mapping", new Runnable() { @Override public void run() {
			mapSharedTracksFolder(newSharedTracksFolder);
			_busy.open();
		}});
	}

	private void mapSharedTracksFolder(File newSharedTracksFolder) {
		try {
			_fileMapper.map(newSharedTracksFolder, "mp3");
			my(TrackDownloader.class).setActive(true);

		} catch (MappingStopped e) {
			my(TrackDownloader.class).setActive(false);
		} catch (IOException e) {
			my(TrackDownloader.class).setActive(false);
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

}
