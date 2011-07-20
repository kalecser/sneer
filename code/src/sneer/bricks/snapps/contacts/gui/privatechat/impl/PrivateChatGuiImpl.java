package sneer.bricks.snapps.contacts.gui.privatechat.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JFrame;

import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.privatechat.PrivateChatGui;

public class PrivateChatGuiImpl implements PrivateChatGui {

	//@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;
	private JFrame _chatWindow;

	{
		//_refToAvoidGc = my(RemoteTuples.class).addSubscription(ChatMessage.class, new Consumer<ChatMessage>() { @Override public void consume(ChatMessage message) {
		//	String reply = ChatWindow.showInputDialog(my(ContactSeals.class).contactGiven(message.publisher).nickname() + " says: " + message.text);
		//	if (reply == null) return;
		//	sendTo(message.publisher, reply);
		//}});

		my(ContactActionManager.class).addContactAction(new ContactAction() {
			@Override
			public void run() {
				displayWindow();
				//Contact contact = my(ContactsGui.class).selectedContact().currentValue();
				//String message = ChatWindow.showInputDialog("Say something to " + contact.nickname());
				//if (message == null) return;
				//Seal to = my(ContactSeals.class).sealGiven(contact).currentValue();
				//sendTo(to, message);
			}

			@Override public String caption() { return "Private chat"; }
			@Override public boolean isVisible() { return true; }
			@Override public boolean isEnabled() { return true; }
			@Override public int positionInMenu() { return 300; }

		});
	}

	private void displayWindow() {
		_chatWindow = new JFrame("Private chat");
		_chatWindow.setVisible(true);
	}
}
