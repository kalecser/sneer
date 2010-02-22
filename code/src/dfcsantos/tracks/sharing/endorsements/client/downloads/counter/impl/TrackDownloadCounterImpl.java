package dfcsantos.tracks.sharing.endorsements.client.downloads.counter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.sharing.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TrackDownloadCounterImpl implements TrackDownloadCounter {

	
	private final Register<Integer> _count = my(Signals.class).newRegister(numberOfTracksDownloadedAlready());

	
	@Override
	public Signal<Integer> count() {
		return _count.output();
	}

	
	@Override
	synchronized
	public void increment() {
		_count.setter().consume(count().currentValue() + 1);
	}

	
	@Override
	synchronized
	public void decrement() {
		_count.setter().consume(count().currentValue() - 1);
	}

	
	private static int numberOfTracksDownloadedAlready() {
		return my(Tracks.class).listMp3FilesFromFolder(peerTracksFolder()).size();
	}

	
	private static File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}

}
