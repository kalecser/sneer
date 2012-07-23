package sneer.bricks.snapps.chat.impl;

import static basis.environments.Environments.my;

import java.awt.Image;

import javax.swing.JFrame;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.images.Images;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.chat.gui.panels.ChatPanels;
import sneer.bricks.snapps.chat.gui.panels.Message;
import basis.lang.Consumer;

class ChatFrame extends JFrame {

	private final Contact contact;
	private ListRegister<Message> messages = my(CollectionSignals.class).newListRegister();
	
	@SuppressWarnings("unused")private final WeakContract refToAvoidGc;
	private static final int TEN_MINUTES = 1000 * 60 * 10;

	ChatFrame(Contact con) {
		this.contact = con;
		getContentPane().add(my(ChatPanels.class).newPanel(messages.output(), new Consumer<String>() { @Override public void consume(String message) {
			if (message == null || message.trim().isEmpty()) return;
			Seal to = my(ContactSeals.class).sealGiven(contact).currentValue();
			sendTo(to, message);
		}}));
		
		refToAvoidGc = my(TupleSpace.class).addSubscription(ChatMessage.class, new Consumer<ChatMessage>() { @Override public void consume(ChatMessage message) {
			if (isPublic(message)) return;
			if (isOld(message)) return;
			messages.add(convert(message));
		}});
		setBounds(0, 0, 200, 300);
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
	
//	private String showInputDialog(String messagePrompt) {
//		return JOptionPane.showInputDialog(messagePrompt);
//	}
	
	static Message convert(final ChatMessage message) {
		return new Message() {
			
			@Override
			public long time() {
				return message.publicationTime;
			}
			
			@Override
			public String text() {
				return message.text;
			}
			
			@Override
			public Image avatar() {
				return isMyOwn(message) 
					? my(Images.class).getImage(getClass().getResource("me.png")) 
					: null;
			}

			private boolean isMyOwn(final ChatMessage message) {
				return message.publisher.equals(my(OwnSeal.class).get().currentValue());
			}
			
			@Override
			public String author() {
				return isMyOwn(message) 
					? ""
					: my(ContactSeals.class).contactGiven(message.publisher).nickname().currentValue();
			}
		};
	}
}
