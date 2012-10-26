package sneer.bricks.network.social.ui.clipboardadder.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.ui.clipboardadder.ClipboardAdder;
import sneer.bricks.skin.widgets.clipboard.Clipboard;
import basis.lang.exceptions.Refusal;

public class ClipboardAdderImpl implements ClipboardAdder {
	private Clipboard clip = my(Clipboard.class);
	private ContactSeals contactSeals = my(ContactSeals.class);
	
	WeakContract timer = my(Timer.class).wakeUpNowAndEvery(3000, new Runnable() { @Override public void run() {
		Seal seal = null;
		try {
			System.err.println(clip.getContent());
			seal = contactSeals.unmarshal(clip.getContent());
		} catch (Refusal e) {
			return;
		}
		
		if(contactSeals.contactGiven(seal) != null) return;

		String nickname = "<New Contact> " + System.currentTimeMillis();
		my(Contacts.class).produceContact(nickname);
		
		try {
			contactSeals.put(nickname, seal);
		} catch (Refusal e) {
			throw new IllegalStateException(e);
		}
	}});
	
}
