package sneer.bricks.network.social.attributes.tests.fixtures.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.attributes.tests.fixtures.AttributeWithDefaultValue;

class AttributeWithDefaultValueImpl implements AttributeWithDefaultValue {

	{
		my(Attributes.class).registerAttribute(AttributeWithDefaultValue.class);
	}

}
