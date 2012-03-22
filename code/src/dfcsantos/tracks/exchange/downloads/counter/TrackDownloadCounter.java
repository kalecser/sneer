package dfcsantos.tracks.exchange.downloads.counter;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface TrackDownloadCounter {

	Signal<Integer> count();
	void refresh();

}
