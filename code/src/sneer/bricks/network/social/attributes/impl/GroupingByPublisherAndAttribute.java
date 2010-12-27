package sneer.bricks.network.social.attributes.impl;

import sneer.bricks.network.social.attributes.AttributeValue;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.Pair;

class GroupingByPublisherAndAttribute implements Functor<AttributeValue, Object> {

	@Override
	public Object evaluate(AttributeValue attributeValue) {
		return Pair.of(attributeValue.publisher, attributeValue.attributeName);
	}

}
