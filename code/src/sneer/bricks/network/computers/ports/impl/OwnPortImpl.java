package sneer.bricks.network.computers.ports.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.computers.ports.OwnPort;
import sneer.bricks.network.social.attributes.Attributes;

class OwnPortImpl implements OwnPort {

	{
		my(Attributes.class).registerAttribute(OwnPort.class);
	}

}
