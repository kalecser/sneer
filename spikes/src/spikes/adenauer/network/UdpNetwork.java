package spikes.adenauer.network;

import sneer.foundation.brickness.Brick;

@Brick
public interface UdpNetwork extends Network {
	static final int MAX_ARRAY_SIZE = 1024 * 20;
}
