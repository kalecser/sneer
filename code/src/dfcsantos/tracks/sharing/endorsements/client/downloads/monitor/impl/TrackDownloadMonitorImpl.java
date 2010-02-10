package dfcsantos.tracks.sharing.endorsements.client.downloads.monitor.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.expression.files.client.Download;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.foundation.lang.Closure;
import dfcsantos.tracks.sharing.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.sharing.endorsements.client.downloads.monitor.TrackDownloadMonitor;

class TrackDownloadMonitorImpl implements TrackDownloadMonitor {

	private static final int PAYLOAD_LIMIT = 3;
//	private static final int TIMEOUT_LIMIT = 15 * 60 * 1000;

	private final Map<Download, Long> _downloads = new ConcurrentHashMap<Download, Long>();

	@Override
	public void watch(final Download download) {
		_downloads.put(download, my(Clock.class).time().currentValue());

		my(Threads.class).startDaemon("Waiting for Download " + _downloads.size(), new Closure() { @Override public void run() {
			try {
				download.waitTillFinished();
				my(TrackDownloadCounter.class).increment();
			} catch (IOException e) {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
			} finally {
				_downloads.remove(download);
			}
		}});
	}

	@Override
	public boolean isOverloaded() {
		return _downloads.size() >= PAYLOAD_LIMIT;
	}

}
