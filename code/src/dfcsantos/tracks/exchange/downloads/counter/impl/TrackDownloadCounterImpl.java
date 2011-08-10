package dfcsantos.tracks.exchange.downloads.counter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.counters.Counter;
import sneer.bricks.pulp.reactive.counters.Counters;
import dfcsantos.tracks.exchange.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TrackDownloadCounterImpl implements TrackDownloadCounter {

	private final Counter _delegate = my(Counters.class).newInstance(numberOfTracksInTheDownloadsFolder());

	@Override
	public Signal<Integer> count() {
		return _delegate.count();
	}

	@Override
	public void increment(boolean condition) {
		_delegate.conditionalIncrementer(condition).run();
	}

	@Override
	public void decrement() {
		_delegate.conditionalDecrementer(_delegate.count().currentValue() > 0).run();
	}

	private int numberOfTracksInTheDownloadsFolder() {
		return downloadsFolder().listFiles(my(DotParts.class).dotPartExclusionFilter()).length;
	}

	private static File downloadsFolder() {
		return my(TracksFolderKeeper.class).inboxFolder();
	}

}
