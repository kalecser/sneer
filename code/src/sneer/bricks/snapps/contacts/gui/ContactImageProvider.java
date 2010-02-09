package sneer.bricks.snapps.contacts.gui;

import java.awt.Image;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;

public interface ContactImageProvider {

	Signal<Image> imageFor(Contact contact);

}
