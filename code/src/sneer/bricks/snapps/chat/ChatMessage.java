package sneer.bricks.snapps.chat;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class ChatMessage extends Tuple {

	public final String text;

	public ChatMessage(Seal addressee_, String text_) {
		super(addressee_);
		this.text = text_;
	}

	public ChatMessage(String phrase) {
		this(null, phrase);
	}

	@Override
	public String toString() {
		return text;
	}
	
}
