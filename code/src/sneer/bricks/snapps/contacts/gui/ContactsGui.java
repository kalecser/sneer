package sneer.bricks.snapps.contacts.gui;

import basis.brickness.Brick;
import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.main.instrumentregistry.Instrument;
import sneer.bricks.software.bricks.snapploader.Snapp;

@Brick(GUI.class)
@Snapp
public interface ContactsGui extends Instrument {

	Signal<Contact> selectedContact();

	void registerContactTextProvider(ContactTextProvider textProvider);

}
