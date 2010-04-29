package sneer.bricks.network.social.attributes.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.AttributeValue;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.serialization.Serializer;
import sneer.foundation.lang.Consumer;

class AttributeSubscriber<T> implements Consumer<AttributeValue> {

	private final Contact _contact;
	private final Signal<Seal> _partySeal;
	private final Class<? extends Attribute<T>> _attribute;
	private final Class<? super T> _valueType;

	private final Register<T> _value = my(Signals.class).newRegister(null);

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;

	AttributeSubscriber(Contact contact, Class<? extends Attribute<T>> attribute, Class<? super T> valueType) {
		_contact = contact;
		_partySeal = sealFor(contact);
		_attribute = attribute;
		_valueType = valueType;

		_toAvoidGC = my(TupleSpace.class).addSubscription(AttributeValue.class, this);
	}

	private Signal<Seal> sealFor(Contact contact) {
		return contact == null ? my(OwnSeal.class).get() : my(ContactSeals.class).sealGiven(contact);
	}

	@Override
	public void consume(AttributeValue tuple) {
		if (!_attribute.getName().equals(tuple.attributeName)) return;

		Seal seal = _partySeal.currentValue();
		if (seal == null) return;
		if (!seal.equals(tuple.publisher)) return;

		deserialize(tuple.serializedValue.copy());
	}

	private void deserialize(byte[] serializedValue) {
		Object deserializedValue;
		try {
			deserializedValue = my(Serializer.class).deserialize(serializedValue);
		} catch (ClassNotFoundException cnfe) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Attribute class not found", "Error deserializing attribute of unexpected type.", cnfe, 7000);
			return;
		}

		if (deserializedValue != null && !_valueType.isInstance(deserializedValue)) {
			String helpMsg = helpMessageFor(deserializedValue.getClass());
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Invalid attribute type received", helpMsg, 7000);
			return;
		}

		_value.setter().consume((T) deserializedValue);
		my(Logger.class).log("New value: {} for: {} attribute received from: {}.", _value.output(), _attribute.getSimpleName(), contact());
	}

	private String helpMessageFor(Class<?> invalidAttributeType) {
		return "Attribute of invalid type '" + invalidAttributeType.getName() + "' received from " + contact() + ". Expected type: '" + _valueType.getName() + "'.";
	}

	private String contact() {
		return (_contact == null) ? "myself" : _contact.nickname().currentValue();
	}

	Signal<T> output() {
		return my(WeakReferenceKeeper.class).keep(_value.output(), this);
	}

}
