package sneer.bricks.network.social.attributes.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.attributes.tests.fixtures.AnotherAttribute;
import sneer.bricks.network.social.attributes.tests.fixtures.AttributeWithDefaultValue;
import sneer.bricks.network.social.attributes.tests.fixtures.SomeAttribute;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

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

	@Test
	public void regsiterAttributes() {
		// Load Attributes
		my(Attributes.class).registerAttribute(AnotherAttribute.class);
		my(Attributes.class).registerAttribute(AttributeWithDefaultValue.class);
		my(Attributes.class).registerAttribute(SomeAttribute.class);

		assertContentsInAnyOrder(my(Attributes.class).all(), AnotherAttribute.class, AttributeWithDefaultValue.class, SomeAttribute.class);
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
