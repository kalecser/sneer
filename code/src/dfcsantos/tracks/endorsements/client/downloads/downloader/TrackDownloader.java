package dfcsantos.tracks.endorsements.client.downloads.downloader;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TrackDownloader {

	void setOnOffSwitch(Signal<Boolean> onOffSwitch);

}
