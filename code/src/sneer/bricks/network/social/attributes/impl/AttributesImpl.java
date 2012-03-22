package sneer.bricks.network.social.attributes.impl;

import static basis.environments.Environments.my;
import basis.lang.Consumer;
import basis.lang.Functor;
import basis.lang.Pair;
import basis.lang.PickyConsumer;
import basis.lang.arrays.ImmutableByteArray;
import basis.lang.exceptions.Refusal;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.AttributeValue;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.bricks.pulp.serialization.Serializer;

class AttributesImpl implements Attributes {

	private SetRegister<Class<? extends Attribute<?>>> _myAttributes;


	private AttributesImpl() {
		_myAttributes = my(CollectionSignals.class).newSetRegister();

		my(TupleSpace.class).keepNewest(AttributeValue.class, new Functor<AttributeValue, Object>() {  @Override public Object evaluate(AttributeValue attributeValue) {
			return Pair.of(attributeValue.publisher, attributeValue.attributeName);
		}});
	}


	@Override
	public <T> Consumer<T> myAttributeSetter(final Class<? extends Attribute<T>> attribute) {
		return new Consumer<T>() { @Override public void consume(T value) {
			my(TupleSpace.class).add(new AttributeValue(null, attribute.getName(), serialize(value)));
			my(Logger.class).log("Setting value of my '{}' attribute to: {}", attribute.getSimpleName(), value);
		}};
	}


	@Override
	public <T> Signal<T> myAttributeValue(final Class<? extends Attribute<T>> attribute) {
		return new AttributeSubscriber<T>(null, attribute, Object.class).output();
	}


	@Override
	public <T> PickyConsumer<T> attributeSetterFor(final Contact contact, final Class<? extends Attribute<T>> attribute) {
		return new PickyConsumer<T>() { @Override public void consume(T value) throws Refusal {
			Seal seal = my(ContactSeals.class).sealGiven(contact).currentValue();
			if (seal == null) throw new Refusal("Unable to set attribute '" + attribute.getSimpleName() + "' to value '" + value + "' because contact '" + contact + "' has no Seal.");
			
			my(TupleSpace.class).add(new AttributeValue(seal, attribute.getName(), serialize(value)));
			my(Logger.class).log("Setting attribute '{}' for contact '{}' to: {}", attribute.getSimpleName(), contact, value);
		}};
	}


	@Override
	public <T> Signal<T> attributeValueFor(final Contact contact, final Class<? extends Attribute<T>> attribute, Class<T> valueType) {
		return new AttributeSubscriber<T>(contact, attribute, valueType).output();
	}


	private ImmutableByteArray serialize(Object value) {
		return new ImmutableByteArray(my(Serializer.class).serialize(value));
	}


	@Override
	public void registerAttribute(Class<? extends Attribute<?>> newAttribute) {
		_myAttributes.add(newAttribute);
	}


	@Override
	public SetSignal<Class<? extends Attribute<?>>> all() {
		return _myAttributes.output();
	}


}
