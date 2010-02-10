package dfcsantos.tracks.sharing.endorsements.client.downloads.monitor;

import sneer.bricks.expression.files.client.Download;
import sneer.foundation.brickness.Brick;

@Brick
public interface TrackDownloadMonitor {

	void watch(Download download);

	boolean isOverloaded();

}
