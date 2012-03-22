package sneer.bricks.pulp.bandwidth;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface BandwidthCounter {

	void sent(int byteCount);
	void received(int byteCount);

	Signal<Integer> uploadSpeedInKBperSecond();
	Signal<Integer> downloadSpeedInKBperSecond();
}