package sneer.bricks.network.social.attributes.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.attributes.tests.fixtures.AnotherAttribute;
import sneer.bricks.network.social.attributes.tests.fixtures.AttributeWithDefaultValue;
import sneer.bricks.network.social.attributes.tests.fixtures.SomeAttribute;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

public class AttributesTest extends BrickTestWithTuples {

	private final Attributes _subject = my(Attributes.class);
	
	@Test
	public void ownAttribute() {
		testOwnAttribute("aValue");
		testOwnAttribute("anotherValue");
		testOwnAttribute(null);
	}

	
	@Test
	public void defaultValue() {
		assertNull(_subject.myAttributeValue(SomeAttribute.class).currentValue());
		assertEquals("Hello", _subject.myAttributeValue(AttributeWithDefaultValue.class).currentValue());
	}

	
	private void testOwnAttribute(String value) {
		_subject.myAttributeSetter(SomeAttribute.class).consume(value);
		assertEquals(value, _subject.myAttributeValue(SomeAttribute.class).currentValue());
	}

	
	@Test
	public void peerAttribute() {
		assertNull(_subject.attributeValueFor(remoteContact(), SomeAttribute.class, String.class).currentValue());

		testPeerAttribute(SomeAttribute.class, "aValue");
		testPeerAttribute(SomeAttribute.class, "anotherValue");
		testPeerAttribute(SomeAttribute.class, null);

		testPeerAttribute(AnotherAttribute.class, 0);
		testPeerAttribute(AnotherAttribute.class, 'X');
		testPeerAttribute(AnotherAttribute.class, "anObject");
		testPeerAttribute(AnotherAttribute.class, null);
	}

	@Test //(timeout = 2000)
	public void regsiterAttributes() {
		final List<Class<? extends Attribute<?>>> attributesToBeLoaded = new ArrayList<Class<? extends Attribute<?>>>(
			Arrays.asList(AnotherAttribute.class, AttributeWithDefaultValue.class, SomeAttribute.class)
		);

		final Latch numberOfRegisteredAttributes = my(Latches.class).produce(attributesToBeLoaded.size());
		my(Attributes.class).all().addReceiver(new Consumer<CollectionChange<Class<? extends Attribute<?>>>>() { @Override public void consume(CollectionChange<Class<? extends Attribute<?>>> registeredAttributes) {
			for (Class<? extends Attribute<?>> registeredAttribute : registeredAttributes.elementsAdded()) {
				assertTrue(attributesToBeLoaded.contains(registeredAttribute));
				attributesToBeLoaded.remove(registeredAttribute);
				numberOfRegisteredAttributes.countDown();				
			}
		}});

		// Load Attributes
		my(AnotherAttribute.class);
		my(AttributeWithDefaultValue.class);
		my(SomeAttribute.class);

		numberOfRegisteredAttributes.waitTillOpen();
	}

	private <T> void testPeerAttribute(Class<? extends Attribute<T>> attribute, T value) {
		setPeerAttribute(attribute, value);
		waitForAllDispatchingToFinish();
		Class<T> valueType = (Class<T>) (value != null ? value.getClass() : Object.class);
		assertEquals(value, _subject.attributeValueFor(remoteContact(), attribute, valueType).currentValue());
	}

	
	private <T> void setPeerAttribute(final Class<? extends Attribute<T>> attribute, final T value) {
		Environments.runWith(remote(), new Closure() { @Override public void run() {
			_subject.myAttributeSetter(attribute).consume(value);
		}});
	}
}
