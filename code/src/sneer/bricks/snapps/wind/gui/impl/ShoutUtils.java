package sneer.bricks.snapps.wind.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.text.SimpleDateFormat;
import java.util.Date;

import sneer.bricks.identity.name.OwnName;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.snapps.chat.ChatMessage;

abstract class ShoutUtils {

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

	private static String ownName() {
		return my(Attributes.class).myAttributeValue(OwnName.class).currentValue();
	}
	
	private static ContactSeals keyManager() {
		return my(ContactSeals.class);
	}

	static String publisherNick(ChatMessage shout) {
		if(isMyOwnShout(shout)) return ownName();
		Contact contact = keyManager().contactGiven(shout.publisher);
		return contact == null
			? "Unknown Public Key: " + shout.publisher + " "
			: contact.nickname().currentValue() + " ";
	}

	static String getFormatedShoutTime(ChatMessage shout) {
		return FORMAT.format(new Date(shout.publicationTime));
	}

	static boolean isMyOwnShout(ChatMessage shout) {
		return my(OwnSeal.class).get().currentValue().equals(shout.publisher);
	}
}
