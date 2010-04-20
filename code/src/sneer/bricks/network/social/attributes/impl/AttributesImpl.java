package sneer.bricks.network.social.attributes.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.AttributeValue;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.serialization.Serializer;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.arrays.ImmutableByteArray;

class AttributesImpl implements Attributes {

	{
		my(TupleSpace.class).keep(AttributeValue.class);
	}

	@Override
	public <T> Signal<T> myAttributeValue(final Class<? extends Attribute<T>> attribute) {
		return new AttributeSubscriber<T>(null, attribute, Object.class).output();
	}

	@Override
	public <T> Signal<T> attributeValueFor(final Contact contact, final Class<? extends Attribute<T>> attribute, Class<T> valueType) {
		return new AttributeSubscriber<T>(contact, attribute, valueType).output();
	}

	@Override
	public <T> Consumer<T> myAttributeSetter(final Class<? extends Attribute<T>> attribute) {
		return new Consumer<T>() { @Override public void consume(T value) {
			my(TupleSpace.class).acquire(new AttributeValue(attribute.getName(), serialize(value)));
		}};
	}

	private ImmutableByteArray serialize(Object value) {
		return new ImmutableByteArray(my(Serializer.class).serialize(value));
	}

}
