package dfcsantos.tracks.monitor;

import sneer.foundation.brickness.Brick;
import dfcsantos.tracks.Track;

@Brick
public interface DownloadedTracksMonitor {

	boolean isTrackAlreadyDownloaded(Track trackToCheck);

}
