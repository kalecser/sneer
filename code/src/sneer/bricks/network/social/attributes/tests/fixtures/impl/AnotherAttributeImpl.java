package sneer.bricks.network.social.attributes.tests.fixtures.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.attributes.tests.fixtures.AnotherAttribute;

class AnotherAttributeImpl implements AnotherAttribute {

	{
		my(Attributes.class).registerAttribute(AnotherAttribute.class);
	}

}
