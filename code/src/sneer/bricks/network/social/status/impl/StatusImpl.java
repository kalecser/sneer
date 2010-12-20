package sneer.bricks.network.social.status.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.status.Status;

class StatusImpl implements Status {

	{
		my(Attributes.class).registerAttribute(Status.class);
	}

}
