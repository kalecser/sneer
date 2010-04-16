package sneer.bricks.network.social.contacts.attributes;

import sneer.bricks.expression.tuples.Tuple;

public class AttributeValue extends Tuple {

	public final String type;

	public final byte[] serializedValue;

	public AttributeValue(String type_, byte[] serializedValue_) {
		type = type_;
		serializedValue = serializedValue_;
	}

}
