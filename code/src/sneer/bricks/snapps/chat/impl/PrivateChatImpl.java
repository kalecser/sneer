package sneer.bricks.snapps.chat.impl;

import static basis.environments.Environments.my;

import javax.swing.JFrame;

import sneer.bricks.network.social.Contact;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.chat.PrivateChat;
import sneer.bricks.snapps.chat.gui.panels.Message;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import basis.lang.CacheMap;
import basis.lang.Producer;

class PrivateChatImpl implements PrivateChat {

	protected CacheMap<Contact, JFrame> framesByContacts = CacheMap.newInstance();

	{
		my(ContactActionManager.class).addContactAction(new ContactAction() { @Override public void run() {
			final Contact contact = my(ContactsGui.class).selectedContact().currentValue();
			framesByContacts.get(contact, new Producer<JFrame>() { @Override public JFrame produce() {
				return new ChatFrame(contact);
			}}).setVisible(true);
		}

		@Override public String caption() { return "Chat"; }
		@Override public boolean isVisible() { return true; }
		@Override public boolean isEnabled() { return true; }
		@Override public int positionInMenu() { return 300; }

		});
	}
	
	@Override
	public Message convert(ChatMessage message) {
		return ChatFrame.convert(message);
	}
}
