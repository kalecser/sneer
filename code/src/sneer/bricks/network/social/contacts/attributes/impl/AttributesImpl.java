package sneer.bricks.network.social.contacts.attributes.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.identity.seals.OwnSeal;
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
import sneer.foundation.lang.Predicate;

// Implement
class AttributesImpl implements Attributes {

	private final ClassLoader apiClassLoader = Attributes.class.getClassLoader();

	@Override
	public <T> Signal<T> myAttributeValue(Class<? extends Attribute<T>> attribute) {
		final Register<T> result = my(Signals.class).newRegister(null);
		WeakContract toAvoidGC = my(TupleSpace.class).addSubscription(AttributeValue.class, new Consumer<AttributeValue>() { @Override public void consume(AttributeValue value) {
			if (my(OwnSeal.class).get().equals(value.publisher)) {
				result.setter().consume((T) deserialize(value.serializedValue));
			}		
		}});
		my(WeakReferenceKeeper.class).keep(result.output(), toAvoidGC);
		return my(WeakReferenceKeeper.class).keep(result.output(), result);
	}

	@Override
	public <T> Signal<T> attributeValueFor(final Contact contact, Class<? extends Attribute<T>> attribute, Predicate<T> filter) {
		final Register<T> result = my(Signals.class).newRegister(null);
		WeakContract toAvoidGC = my(TupleSpace.class).addSubscription(AttributeValue.class, new Consumer<AttributeValue>() { @Override public void consume(AttributeValue value) {
			if (my(ContactSeals.class).sealGiven(contact).equals(value.publisher)) {
				result.setter().consume((T) deserialize(value.serializedValue));
			}		
		}});
		my(WeakReferenceKeeper.class).keep(result.output(), toAvoidGC);
		return my(WeakReferenceKeeper.class).keep(result.output(), result);
	}

	@Override
	public <T> Consumer<T> myAttributeSetter(Class<? extends Attribute<T>> attribute) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	private Object deserialize(byte[] serializedValue) {
		try {
			return my(Serializer.class).deserialize(serializedValue, apiClassLoader);
		} catch (ClassNotFoundException ignored) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(ignored); // Fix Handle this exception.
		}
	}

}
