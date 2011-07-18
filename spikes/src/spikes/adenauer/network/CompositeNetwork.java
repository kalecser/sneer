package spikes.adenauer.network;

import sneer.foundation.brickness.Brick;

@Brick
public interface CompositeNetwork extends Network {
	void add(Network network);
}
