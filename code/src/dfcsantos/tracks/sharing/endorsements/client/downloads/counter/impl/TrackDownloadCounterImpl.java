package dfcsantos.tracks.sharing.endorsements.client.downloads.counter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Closure;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.sharing.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class TrackDownloadCounterImpl implements TrackDownloadCounter {

	private final Register<Integer> _numberOfDownloadedTracks = my(Signals.class).newRegister(numberOfTracksDownloadedAlready());

	@SuppressWarnings("unused") private final WeakContract _timerContract;

	{
		_timerContract = my(Timer.class).wakeUpEvery(15 * 60 * 1000, new Closure() { @Override public void run() {
			synchronizeWithFileSystem();
		}});
	}

	private void synchronizeWithFileSystem() {
		_numberOfDownloadedTracks.setter().consume(numberOfTracksDownloadedAlready());
	}

	@Override
	public Signal<Integer> count() {
		return _numberOfDownloadedTracks.output();
	}

	@Override
	public void increment() {
		_numberOfDownloadedTracks.setter().consume(count().currentValue() + 1);
	}

	@Override
	public void decrement() {
		_numberOfDownloadedTracks.setter().consume(count().currentValue() - 1);
	}

	private static int numberOfTracksDownloadedAlready() {
		return my(Tracks.class).listMp3FilesFromFolder(peerTracksFolder()).size();
	}

	private static File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}

}
