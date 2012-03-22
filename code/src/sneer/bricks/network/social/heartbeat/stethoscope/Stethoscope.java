package sneer.bricks.network.social.heartbeat.stethoscope;

import basis.brickness.Brick;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface Stethoscope {

	Signal<Boolean> isAlive(Contact contact);
	
}
