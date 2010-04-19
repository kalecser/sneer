package sneer.bricks.network.social.attributes.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.AttributeValue;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.serialization.Serializer;
import sneer.foundation.lang.Consumer;

class AttributesImpl implements Attributes {

	private final ClassLoader apiClassLoader = Attributes.class.getClassLoader();

	{
		my(TupleSpace.class).keep(AttributeValue.class);
	}

	@Override
	public <T> Signal<T> myAttributeValue(final Class<? extends Attribute<T>> attribute) {
		return attributeValueFor(my(OwnSeal.class).get(), attribute);
	}

	@Override
	public <T> Signal<T> attributeValueFor(final Contact contact, final Class<? extends Attribute<T>> attribute) {
		return attributeValueFor(my(ContactSeals.class).sealGiven(contact), attribute);
	}

	@Override
	public <T> Consumer<T> myAttributeSetter(final Class<? extends Attribute<T>> attribute) {
		return new Consumer<T>() { @Override public void consume(T value) {
			my(TupleSpace.class).acquire(new AttributeValue(attribute.getName(), serialize(value)));
		}};
	}

	private <T> Signal<T> attributeValueFor(final Signal<Seal> partySignal, final Class<? extends Attribute<T>> attribute) {
		final Register<T> result = my(Signals.class).newRegister(null);
		WeakContract toAvoidGC = my(TupleSpace.class).addSubscription(AttributeValue.class, new Consumer<AttributeValue>() { @Override public void consume(AttributeValue tuple) {
			if (!attribute.getName().equals(tuple.attributeName)) return;

			Seal party = partySignal.currentValue();
			if (party != null && party.equals(tuple.publisher))
				result.setter().consume((T) deserialize(tuple.serializedValue.copy()));
		}});
		my(WeakReferenceKeeper.class).keep(result.output(), toAvoidGC);
		return my(WeakReferenceKeeper.class).keep(result.output(), result);
	}

	private ImmutableByteArray serialize(Object value) {
		return my(ImmutableArrays.class).newImmutableByteArray(my(Serializer.class).serialize(value));
	}

	private Object deserialize(byte[] serializedValue) {
		try {
			return my(Serializer.class).deserialize(serializedValue, apiClassLoader);
		} catch (ClassNotFoundException ignored) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(ignored); // Fix Handle this exception.
		}
	}

}
