package sneer.bricks.snapps.chat.impl;

import static basis.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.gui.trayicon.TrayIcons;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.chat.Chat;
import sneer.bricks.snapps.chat.gui.panels.Message;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import basis.lang.CacheMap;
import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.Producer;

class ChatImpl implements Chat {

	private static final int TEN_MINUTES = 1000 * 60 * 10;
	
	protected CacheMap<Contact, ChatFrame> framesByContact = CacheMap.newInstance();

	@SuppressWarnings("unused") private Object refToAvoidGc;

	private Contact lastContact = null;

	{
		my(ContactActionManager.class).addContactAction(new ContactAction() { @Override public void run() {
			final Contact contact = my(ContactsGui.class).selectedContact().currentValue();
			showFrameFor(contact);
		}

		@Override public String caption() { return "Chat"; }
		@Override public boolean isVisible() { return true; }
		@Override public boolean isEnabled() { return true; }
		@Override public int positionInMenu() { return 300; }
		});
		
		handleReceivedMessages();
		handleTrayIconBaloonAction();
	}

	@Override
	public Message convert(ChatMessage message) {
		return ChatFrame.convert(message);
	}

	private void handleReceivedMessages() {
		refToAvoidGc = my(TupleSpace.class).addSubscription(ChatMessage.class, new Consumer<ChatMessage>() { @Override public void consume(ChatMessage message) {
			if (isPublic(message)) return;
			if (isOld(message)) return;
			
			if (isByMe(message))
				showMessage(message, message.addressee);
			else 
				showMessage(message, message.publisher);
		}});
	}
	
	private void handleTrayIconBaloonAction() {
		my(TrayIcons.class).addActionListener(new Closure() {  @Override public void run() {
			if (lastContact == null) return;
			showFrameFor(lastContact);
		}});
	}

	private boolean isPublic(ChatMessage message) {		
		return message.addressee == null;
	}

	
	private boolean isOld(ChatMessage message) {
		return now() - message.publicationTime > TEN_MINUTES;
	}

	
	private long now() {
		return my(Clock.class).time().currentValue();
	}
	
	private ChatFrame frameFor(final Contact contact) {
		return framesByContact.get(contact, new Producer<ChatFrame>() { @Override public ChatFrame produce() {
			return new ChatFrame(contact);
		}});
	}

	private void showMessage(ChatMessage message, Seal sealOfContact) {
		Contact contact = my(ContactSeals.class).contactGiven(sealOfContact);
		if (contact == null) return;
		lastContact  = contact;
		frameFor(contact).showMessage(convert(message));
	}

	private boolean isByMe(ChatMessage message) {
		return message.publisher.equals(my(OwnSeal.class).get().currentValue());
	}

	private void showFrameFor(final Contact contact) {
		frameFor(contact).show();
	}
}
