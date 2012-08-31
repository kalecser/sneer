package sneer.bricks.network.computers.channels;

import sneer.bricks.network.social.Contact;
import basis.brickness.Brick;

@Brick
public interface Channels {

	public enum Priority { LOWEST, LOW, NORMAL, HIGH, HIGHEST };

	boolean READY_FOR_PRODUCTION = false;

	Channel accept(long id);
	
	Channel create(Contact contact, Priority priority);
	
	Channel createControl(Contact contact);

}
