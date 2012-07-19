package sneer.bricks.network.computers.addresses.own.host.impl;

import static basis.environments.Environments.my;
import sneer.bricks.network.computers.addresses.own.host.OwnHost;
import sneer.bricks.network.social.attributes.Attributes;

class OwnHostImpl implements OwnHost {

	{
		my(Attributes.class).registerAttribute(OwnHost.class);
	}

}
