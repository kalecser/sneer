package dfcsantos.tracks.sharing.endorsements.client.downloads;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TrackDownloader {

	void setActive(boolean isActive);

	Signal<Integer> numberOfDownloadedTracks();
	void decrementDownloadedTracks();

}
