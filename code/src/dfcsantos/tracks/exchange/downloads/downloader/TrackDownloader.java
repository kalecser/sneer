package dfcsantos.tracks.exchange.downloads.downloader;

import basis.brickness.Brick;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.SetSignal;

@Brick
public interface TrackDownloader {

	void setOnOffSwitch(Signal<Boolean> onOffSwitch);

	SetSignal<Download> runningDownloads();

}
