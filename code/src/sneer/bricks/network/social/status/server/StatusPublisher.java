package sneer.bricks.network.social.status.server;

import sneer.foundation.brickness.Brick;

@Brick
public interface StatusPublisher {

	void publish(String status);

}
