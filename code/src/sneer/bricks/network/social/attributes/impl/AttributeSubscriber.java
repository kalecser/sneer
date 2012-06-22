package sneer.bricks.network.social.attributes.impl;

import static basis.environments.Environments.my;

import java.lang.reflect.Field;

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

class AttributeSubscriber<T> {

	private final Contact _contact;
	private final Signal<Seal> _partySeal;
	private final Class<? extends Attribute<T>> _attribute;
	private final Class<? super T> _valueType;

	private final Register<T> _value;


	AttributeSubscriber(Class<? extends Attribute<T>> attribute) {
		this(null, attribute, Object.class);
	}

	AttributeSubscriber(Contact contact, Class<? extends Attribute<T>> attribute, Class<? super T> valueType) {
		_contact = contact;
		_partySeal = sealFor(_contact);
		_attribute = attribute;
		_valueType = valueType;

		_value = my(Signals.class).newRegister(defaultValue());
	}

	
	void handle(AttributeValue tuple) {
		if (!_attribute.getName().equals(tuple.attributeName)) return;

		Seal seal = _partySeal.currentValue();
		if (seal == null) return;
		if (!seal.equals(tuple.publisher)) return;

		deserialize(tuple.serializedValue.copy());
	}

	
	private T defaultValue() {
		try {
			Field field = _attribute.getField("DEFAULT");
			return (T) field.get(null);
		} catch (Exception e) {
			throw new IllegalStateException("Exception trying to get DEFAULT value from Attribute: " + _attribute, e);
		}
	}

	
	private Signal<Seal> sealFor(Contact contact) {
		return contact == null ? my(OwnSeal.class).get() : my(ContactSeals.class).sealGiven(contact);
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
