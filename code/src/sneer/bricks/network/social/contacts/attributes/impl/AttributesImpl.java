package sneer.bricks.network.social.contacts.attributes.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.network.social.contacts.attributes.Attribute;
import sneer.bricks.network.social.contacts.attributes.AttributeValue;
import sneer.bricks.network.social.contacts.attributes.Attributes;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.serialization.Serializer;
import sneer.foundation.lang.Consumer;

// Implement
class AttributesImpl implements Attributes {

	private final ClassLoader apiClassLoader = Attributes.class.getClassLoader();

	@Override
	public <T> Signal<T> myAttributeValue(final Class<? extends Attribute<T>> attribute) {
		return attributeValueGiven(my(OwnSeal.class).get(), attribute);
	}

	@Override
	public <T> Signal<T> attributeValueFor(final Contact contact, final Class<? extends Attribute<T>> attribute) {
		return attributeValueGiven(my(ContactSeals.class).sealGiven(contact).currentValue(), attribute);
	}

	@Override
	public <T> Consumer<T> myAttributeSetter(final Class<? extends Attribute<T>> attribute) {
		return new Consumer<T>() { @Override public void consume(T value) {
			my(TupleSpace.class).acquire(new AttributeValue(attribute.getName(), serialize(value)));
		}};
	}

	private <T> Signal<T> attributeValueGiven(final Seal contactSeal, final Class<? extends Attribute<T>> attribute) {
		final Register<T> result = my(Signals.class).newRegister(null);
		WeakContract toAvoidGC = my(TupleSpace.class).addSubscription(AttributeValue.class, new Consumer<AttributeValue>() { @Override public void consume(AttributeValue value) {
			if (hasTheExpectedType(value, attribute)) return;
			if (contactSeal.equals(value.publisher))
				result.setter().consume((T) deserialize(value.serializedValue));
		}});
		my(WeakReferenceKeeper.class).keep(result.output(), toAvoidGC);
		return my(WeakReferenceKeeper.class).keep(result.output(), result);
	}

	private boolean hasTheExpectedType(AttributeValue value, final Class<? extends Attribute<?>> expectedType) {
		try {
			return apiClassLoader.loadClass(value.type).isAssignableFrom(expectedType);
		} catch (ClassNotFoundException cnfe) {
			my(Logger.class).log("Attribute value of unkown type received: ", value.type);
			return false;
		}
	}

	private byte[] serialize(Object value) {
		return my(Serializer.class).serialize(value);
	}

	private Object deserialize(byte[] serializedValue) {
		try {
			return my(Serializer.class).deserialize(serializedValue, apiClassLoader);
		} catch (ClassNotFoundException ignored) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(ignored); // Fix Handle this exception.
		}
	}

}
