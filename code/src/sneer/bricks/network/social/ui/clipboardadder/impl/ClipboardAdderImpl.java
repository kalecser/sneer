package sneer.bricks.network.social.ui.clipboardadder.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.ui.clipboardadder.ClipboardAdder;
import sneer.bricks.skin.widgets.clipboard.Clipboard;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import sneer.bricks.snapps.contacts.gui.info.ContactInfoWindow;
import basis.lang.exceptions.Refusal;

public class ClipboardAdderImpl implements ClipboardAdder {
	private Clipboard clip = my(Clipboard.class);
	private ContactSeals contactSeals = my(ContactSeals.class);
	private Seal lastSeal;
	
	WeakContract timer = my(Timer.class).wakeUpNowAndEvery(3000, new Runnable() { @Override public void run() {
		Seal seal = null;
		try {
 			seal = contactSeals.unmarshal(clip.getContent());
		} catch (Refusal e) {
			return;
		}
		
		if(seal == null) return;		
		if(contactSeals.contactGiven(seal) != null) return;
		if(seal.equals(lastSeal)) return;
		if(my(OwnSeal.class).get().equals(seal)) return;
		
		lastSeal = seal;
		String nickname = "<New Contact> " + System.currentTimeMillis();
		Contact contact = my(Contacts.class).produceContact(nickname);
		try {
			contactSeals.put(nickname, seal);
		} catch (Refusal e) {
			throw new IllegalStateException(e);
		}
		
		my(ContactsGui.class).setSelectedContact(contact);
		my(ContactInfoWindow.class).open();
	}});
	
}
