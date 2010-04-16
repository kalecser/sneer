package sneer.bricks.snapps.contacts.gui;

import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.pulp.reactive.Signal;

public interface ContactTextProvider {

	enum Position { LEFT, CENTER, RIGHT }; 

	Signal<String> textFor(Contact contact);

	Position position();

}
