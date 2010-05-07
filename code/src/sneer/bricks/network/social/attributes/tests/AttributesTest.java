package sneer.bricks.network.social.attributes.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.pump.TuplePump;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePumps;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attribute;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.attributes.tests.fixtures.AnotherAttribute;
import sneer.bricks.network.social.attributes.tests.fixtures.AttributeWithDefaultValue;
import sneer.bricks.network.social.attributes.tests.fixtures.SomeAttribute;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.exceptions.Refusal;

public class AttributesTest extends BrickTest {

	private final Attributes _subject = my(Attributes.class);

	private Environment _remoteEnvironment;
	private Contact _remoteContact;
	private TuplePump _tuplePump;

	
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
	public void peerAttribute() throws Refusal {
		_remoteEnvironment	= configureRemoteEnvironment();
		_remoteContact		= configureRemoteContact();

		assertNull(_subject.attributeValueFor(_remoteContact, SomeAttribute.class, String.class).currentValue());

		testPeerAttribute(SomeAttribute.class, "aValue");
		testPeerAttribute(SomeAttribute.class, "anotherValue");
		testPeerAttribute(SomeAttribute.class, null);

		testPeerAttribute(AnotherAttribute.class, 0);
		testPeerAttribute(AnotherAttribute.class, 'X');
		testPeerAttribute(AnotherAttribute.class, "anObject");
		testPeerAttribute(AnotherAttribute.class, null);

		crash(_remoteEnvironment);
	}

	
	private Environment configureRemoteEnvironment() {
		Environment remote = newTestEnvironment();
		configureStorageFolder(remote, "remote/data");
		_tuplePump = my(TuplePumps.class).startPumpingWith(remote);
		return remote;
	}

	
	private Contact configureRemoteContact() throws Refusal {
		Contact peer = my(Contacts.class).addContact("Peer");
		Seal peerSeal = EnvironmentUtils.produceIn(_remoteEnvironment, new Producer<Seal>() { @Override public Seal produce() {
			return my(OwnSeal.class).get().currentValue();
		}});
		my(ContactSeals.class).put("Peer",	peerSeal);
		return peer;
	}

	
	private <T> void testPeerAttribute(Class<? extends Attribute<T>> attribute, T value) {
		setPeerAttribute(attribute, value);
		_tuplePump.waitForAllDispatchingToFinish();
		Class<T> valueType = (Class<T>) (value != null ? value.getClass() : Object.class);
		assertEquals(value, _subject.attributeValueFor(_remoteContact, attribute, valueType).currentValue());
	}

	
	private <T> void setPeerAttribute(final Class<? extends Attribute<T>> attribute, final T value) {
		Environments.runWith(_remoteEnvironment, new Closure() { @Override public void run() {
			my(Attributes.class).myAttributeSetter(attribute).consume(value);
		}});
	}
}
