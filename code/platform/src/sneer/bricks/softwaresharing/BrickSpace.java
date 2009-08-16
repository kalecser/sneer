package sneer.bricks.softwaresharing;

import java.util.Collection;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface BrickSpace {

	EventSource<Contact> newBrickConfigurationFound();

	Collection<BrickInfo> availableBricks();
	
}
