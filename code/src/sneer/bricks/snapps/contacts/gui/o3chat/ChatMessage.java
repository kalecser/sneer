package sneer.bricks.snapps.contacts.gui.o3chat;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class ChatMessage extends Tuple {

	public final String message;

	public ChatMessage(Seal addressee_, String message_) {
		super(addressee_);
		this.message = message_;
	}

}
