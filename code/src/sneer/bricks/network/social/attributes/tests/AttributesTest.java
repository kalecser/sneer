package sneer.bricks.network.social.attributes.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.attributes.tests.fixtures.SomeAttribute;
import sneer.bricks.software.folderconfig.tests.BrickTest;

@Ignore
public class AttributesTest extends BrickTest {

	private final Attributes _subject = my(Attributes.class);

	@Test
	public void myAttribute() {
		assertNull(_subject.myAttributeValue(SomeAttribute.class).currentValue());

		testAttributeValue("1st value");
		testAttributeValue("2nd value");
		testAttributeValue(null);
	}

	private void testAttributeValue(String value) {
		_subject.myAttributeSetter(SomeAttribute.class).consume(value);
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		assertEquals(value, _subject.myAttributeValue(SomeAttribute.class).currentValue());
	}

}
