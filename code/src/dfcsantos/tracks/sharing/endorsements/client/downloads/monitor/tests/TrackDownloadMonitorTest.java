package dfcsantos.tracks.sharing.endorsements.client.downloads.monitor.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.files.client.Download;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import dfcsantos.tracks.sharing.endorsements.client.downloads.monitor.TrackDownloadMonitor;

public class TrackDownloadMonitorTest extends BrickTest {

	private final TrackDownloadMonitor _subject = my(TrackDownloadMonitor.class);

	@Ignore
	@Test
	public void downloadTimeout() {
		_subject.watch(new Download() { 
			@Override
			public void waitTillFinished() {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
			}

			@Override
			public void dispose() {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
			}
		});

		my(Clock.class).advanceTime(15 * 60 * 1000);
	}

}
