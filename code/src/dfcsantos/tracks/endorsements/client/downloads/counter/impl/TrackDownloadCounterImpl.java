package dfcsantos.tracks.endorsements.client.downloads.counter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.counters.Counter;
import sneer.bricks.pulp.reactive.counters.Counters;
import sneer.foundation.lang.Closure;
import dfcsantos.tracks.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TrackDownloadCounterImpl implements TrackDownloadCounter {

	private final Counter _delegate = my(Counters.class).newInstance(numberOfTracksInTheDownloadsFolder());

	@Override
	public Signal<Integer> count() {
		return _delegate.count();
	}

	@Override
	public Closure incrementer() {
		return _delegate.incrementer();
	}

	@Override
	public Closure conditionalIncrementer(boolean condition) {
		return _delegate.conditionalIncrementer(condition);
	}

	@Override
	public Closure decrementer() {
		return _delegate.decrementer();
	}

	@Override
	public Closure conditionalDecrementer(boolean condition) {
		return _delegate.conditionalDecrementer(condition);
	}

	private int numberOfTracksInTheDownloadsFolder() {
		return downloadsFolder().listFiles(my(DotParts.class).dotPartExclusionFilter()).length;
	}

	private static File downloadsFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}

}
