package sneer.bricks.pulp.bandwidth;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface BandwidthCounter {

	void sent(int byteCount);
	void received(int byteCount);

	Signal<Integer> uploadSpeedInKBperSecond();
	Signal<Integer> downloadSpeedInKBperSecond();
}