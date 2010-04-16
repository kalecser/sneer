package sneer.bricks.network.social.contacts.attributes.status.publisher;

import sneer.foundation.brickness.Brick;

@Brick
public interface StatusPublisher {

	void publish(String status);

}
