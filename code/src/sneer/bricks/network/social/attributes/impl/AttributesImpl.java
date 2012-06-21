package sneer.bricks.network.social.attributes.impl;

import static basis.environments.Environments.my;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
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
import basis.lang.Consumer;
import basis.lang.Functor;
import basis.lang.Pair;
import basis.lang.PickyConsumer;
import basis.lang.arrays.ImmutableByteArray;
import basis.lang.exceptions.Refusal;

class AttributesImpl implements Attributes {

	private static final WeakReference<AttributeSubscriber<?>>[] EMPTY_ARRAY = new WeakReference[0];

	private SetRegister<Class<? extends Attribute<?>>> _myAttributes;

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;

	private final Object monitor = new Object();
	private final List<WeakReference<AttributeSubscriber<?>>> subscribers = new ArrayList<WeakReference<AttributeSubscriber<?>>>();
	private final List<WeakReference<AttributeValue>> liveTuples = new ArrayList<WeakReference<AttributeValue>>();


	private AttributesImpl() {
		_myAttributes = my(CollectionSignals.class).newSetRegister();

		my(TupleSpace.class).keepNewest(AttributeValue.class, new Functor<AttributeValue, Object>() {  @Override public Object evaluate(AttributeValue attributeValue) {
			return Pair.of(attributeValue.publisher, attributeValue.attributeName);
		}});
		
		_toAvoidGC = my(TupleSpace.class).addSubscription(AttributeValue.class, new Consumer<AttributeValue>() { @Override public void consume(AttributeValue tuple) {
			handle(tuple);
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
	public <T> Signal<T> myAttributeValue(Class<? extends Attribute<T>> attribute) {
		AttributeSubscriber<T> ret = new AttributeSubscriber<T>(null, attribute, Object.class);
		synchronized (monitor) {
			subscribers.add(new WeakReference<AttributeSubscriber<?>>(ret));
			Iterator<WeakReference<AttributeValue>> it = liveTuples.iterator();
			while (it.hasNext()) {
				WeakReference<AttributeValue> ref = it.next();
				AttributeValue tuple = ref.get();
				if (tuple == null)
					it.remove();
				else
					ret.handle(tuple);
			}
		}
		return ret.output();
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


	private void handle(AttributeValue tuple) {
		WeakReference<AttributeSubscriber<?>>[] currentSubscribers;
		synchronized (monitor) { 
			currentSubscribers = subscribers.toArray(EMPTY_ARRAY);
			liveTuples.add(new WeakReference<AttributeValue>(tuple));
		}
		for (WeakReference<AttributeSubscriber<?>> ref : currentSubscribers) {
			AttributeSubscriber<?> subscriber = ref.get();
			if (subscriber == null)
				subscribers.remove(ref);
			else
				subscriber.handle(tuple);
		}
	}


}
