package dfcsantos.tracks.exchange.downloads.downloader;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TrackDownloader {

	void setOnOffSwitch(Signal<Boolean> onOffSwitch);

	SetSignal<Download> runningDownloads();

}
