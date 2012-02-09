package dfcsantos.tracks.exchange.downloads.counter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import dfcsantos.tracks.exchange.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TrackDownloadCounterImpl implements TrackDownloadCounter {

	private final Register<Integer> count = my(Signals.class).newRegister(0);
	{
		refresh();
	}
	
	
	@Override
	public Signal<Integer> count() {
		return count.output();
	}
	
	
	@Override
	public void refresh() {
		count.setter().consume(numberOfTracksInTheDownloadsFolder());
	}

	private int numberOfTracksInTheDownloadsFolder() {
		return downloadsFolder().listFiles(my(DotParts.class).dotPartExclusionFilter()).length;
	}

	private static File downloadsFolder() {
		return my(TracksFolderKeeper.class).inboxFolder();
	}

}
