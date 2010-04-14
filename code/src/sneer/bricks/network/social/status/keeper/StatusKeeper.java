package sneer.bricks.network.social.status.keeper;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface StatusKeeper {

	Signal<String> status(Contact contact);

	void setStatus(Contact contact, String status);

}
