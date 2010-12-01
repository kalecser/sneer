package sneer.bricks.network.social.attributes;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.lang.arrays.ImmutableByteArray;

public class AttributeValue extends Tuple {

	public final String attributeName;

	public final ImmutableByteArray serializedValue;

	public AttributeValue(Seal addressee_, String attributeName_, ImmutableByteArray serializedValue_) {
		super(addressee_);
		attributeName = attributeName_;
		serializedValue = serializedValue_;
	}

}
