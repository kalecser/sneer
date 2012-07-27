package sneer.bricks.snapps.chat.impl;

import static basis.environments.Environments.my;

import java.awt.Image;

import javax.swing.JFrame;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.gui.images.Images;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.chat.gui.panels.ChatPanels;
import sneer.bricks.snapps.chat.gui.panels.Message;
import basis.lang.Consumer;

class ChatFrame {

	private final Contact contact;
	private ListRegister<Message> messages = my(CollectionSignals.class).newListRegister();
	
	private final JFrame delegate;
	
	ChatFrame(Contact con) {
		this.contact = con;
		delegate = my(ReactiveWidgetFactory.class).newFrame(con.nickname()).getMainWidget();
		delegate.getContentPane().add(my(ChatPanels.class).newPanel(messages.output(), new Consumer<String>() { @Override public void consume(String message) {
			if (message == null || message.trim().isEmpty()) return;
			Seal to = my(ContactSeals.class).sealGiven(contact).currentValue();
			sendTo(to, message);
		}}));
		
		delegate.setBounds(100, 100, 400, 600);
	}
	
	private void sendTo(Seal to, String text) {
		my(TupleSpace.class).add(new ChatMessage(to, text));
	}
	
	static Message convert(final ChatMessage message) {
		return new Message() {
			@Override
			public Image avatar() {
				return isByMe() 
					? my(Images.class).getImage(getClass().getResource("me.png")) 
					: null;
			}

			@Override
			public String author() {
				return isByMe() 
					? ""
					: my(ContactSeals.class).contactGiven(message.publisher).nickname().currentValue();
			}
			@Override
			public boolean isByMe() {
				return message.publisher.equals(my(OwnSeal.class).get().currentValue());
			}

			@Override
			public long time() {
				return message.publicationTime;
			}
			
			@Override
			public String text() {
				return message.text;
			}
		};
	}

	void show() {
		delegate.setVisible(true);
	}

	public void showMessage(Message message) {
		messages.add(message);
	}
}
