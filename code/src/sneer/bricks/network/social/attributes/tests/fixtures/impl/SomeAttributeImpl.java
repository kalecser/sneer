package sneer.bricks.network.social.attributes.tests.fixtures.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.attributes.tests.fixtures.SomeAttribute;

class SomeAttributeImpl implements SomeAttribute {

	{
		my(Attributes.class).registerAttribute(SomeAttribute.class);
	}

}
