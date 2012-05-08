package sneer.bricks.snapps.chat.impl;

import static basis.environments.Environments.my;

import javax.swing.JOptionPane;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.chat.PrivateChat;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import basis.lang.Consumer;

class PrivateChatImpl implements PrivateChat {

	private static final int TEN_MINUTES = 1000 * 60 * 10;
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;

	{
		_refToAvoidGc = my(RemoteTuples.class).addSubscription(ChatMessage.class, new Consumer<ChatMessage>() { @Override public void consume(ChatMessage message) {
			if (isPublic(message)) return;
			if (isOld(message)) return;
			String messagePrompt = my(ContactSeals.class).contactGiven(message.publisher).nickname() + " says: " + message.text;
			String reply = showInputDialog(messagePrompt);
			if (reply == null) return;
			sendTo(message.publisher, reply);
		}});

		my(ContactActionManager.class).addContactAction(new ContactAction() {
			@Override
			public void run() {
				Contact contact = my(ContactsGui.class).selectedContact().currentValue();
				String message = showInputDialog("Say something to " + contact.nickname());
				if (message == null || message.trim().isEmpty()) return;
				Seal to = my(ContactSeals.class).sealGiven(contact).currentValue();
				sendTo(to, message);
			}

			@Override public String caption() { return "Chat"; }
			@Override public boolean isVisible() { return true; }
			@Override public boolean isEnabled() { return true; }
			@Override public int positionInMenu() { return 300; }

		});
	}

	private void sendTo(Seal to, String text) {
		my(TupleSpace.class).add(new ChatMessage(to, text));
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
	
	private String showInputDialog(String messagePrompt) {
		return JOptionPane.showInputDialog(messagePrompt);
	}

}
