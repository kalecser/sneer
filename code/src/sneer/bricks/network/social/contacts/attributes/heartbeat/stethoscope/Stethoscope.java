package sneer.bricks.network.social.contacts.attributes.heartbeat.stethoscope;

import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface Stethoscope {

	Signal<Boolean> isAlive(Contact contact);
	
}
