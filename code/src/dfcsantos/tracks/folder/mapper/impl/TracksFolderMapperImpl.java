package dfcsantos.tracks.folder.mapper.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;
import dfcsantos.tracks.folder.mapper.TracksFolderMapper;

class TracksFolderMapperImpl implements TracksFolderMapper {

	private Latch _sharedTracksMapping = my(Latches.class).produce();
	private Latch _peerTracksMapping = my(Latches.class).produce();

	@SuppressWarnings("unused") private Object _sharedTracksFolderConsumerContract;

	@Override
	public void startMapping() {
		startPeerTracksMapping();
		_sharedTracksFolderConsumerContract = my(TracksFolderKeeper.class).sharedTracksFolder().addReceiver(new Consumer<File>() { @Override public void consume(File sharedTracksFolder) {
			startSharedTracksMapping();
		}});
	}

	private void startPeerTracksMapping() {
		my(Threads.class).startDaemon("Peer Tracks Folder Mapping", new Runnable() { @Override public void run() {
			map(peerTracksFolder());
			_peerTracksMapping.open();
		}});
	}

	private File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}

	private void startSharedTracksMapping() {
		my(Threads.class).startDaemon("Shared Tracks Folder Mapping", new Runnable() { @Override public void run() {
			map(sharedTracksFolder());
			_sharedTracksMapping.open();
		}});
	}

	private File sharedTracksFolder() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
	}

	private void map(final File tracksFolder) {		
		try {
			my(FileMap.class).put(tracksFolder, "mp3");
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	@Override
	public void waitMapping() {
		_peerTracksMapping.waitTillOpen();
		_sharedTracksMapping.waitTillOpen();
	}

}
