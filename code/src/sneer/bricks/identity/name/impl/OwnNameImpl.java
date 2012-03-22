package sneer.bricks.identity.name.impl;

import static basis.environments.Environments.my;
import sneer.bricks.identity.name.OwnName;
import sneer.bricks.network.social.attributes.Attributes;

class OwnNameImpl implements OwnName {

	{
		my(Attributes.class).registerAttribute(OwnName.class);
	}
	
}
