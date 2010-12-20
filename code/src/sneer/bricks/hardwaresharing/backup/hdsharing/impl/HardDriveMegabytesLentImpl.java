package sneer.bricks.hardwaresharing.backup.hdsharing.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardwaresharing.backup.hdsharing.HardDriveMegabytesLent;
import sneer.bricks.network.social.attributes.Attributes;

class HardDriveMegabytesLentImpl implements HardDriveMegabytesLent {

	{
		my(Attributes.class).registerAttribute(HardDriveMegabytesLent.class);
	}

}
