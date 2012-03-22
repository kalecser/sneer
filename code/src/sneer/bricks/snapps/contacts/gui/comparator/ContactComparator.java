package sneer.bricks.snapps.contacts.gui.comparator;

import java.util.Comparator;

import basis.brickness.Brick;

import sneer.bricks.network.social.Contact;

@Brick
public interface ContactComparator extends Comparator<Contact> {

	@Override
	int compare(Contact contact1, Contact contact2);

}