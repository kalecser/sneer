package sneer.bricks.snapps.contacts.gui;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.main.instrumentregistry.Instrument;
import sneer.foundation.brickness.Brick;

@Brick //(GUI.class)
public interface ContactsGui extends Instrument {

	Signal<Contact> selectedContact();

}
