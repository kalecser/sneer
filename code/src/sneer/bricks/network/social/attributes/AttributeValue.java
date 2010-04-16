package sneer.bricks.network.social.attributes;

import sneer.bricks.expression.tuples.Tuple;

public class AttributeValue extends Tuple {

	public final String attributeName;

	public final byte[] serializedValue;

	public AttributeValue(String attributeName_, byte[] serializedValue_) {
		attributeName = attributeName_;
		serializedValue = serializedValue_;
	}

}
