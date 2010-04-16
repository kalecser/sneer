package dfcsantos.tracks.endorsements.client.downloads.counter;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TrackDownloadCounter {

	Signal<Integer> count();

	void increment(boolean condition);

	void decrement();

}
