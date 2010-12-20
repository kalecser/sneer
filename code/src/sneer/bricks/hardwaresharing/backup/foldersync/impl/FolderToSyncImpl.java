package sneer.bricks.hardwaresharing.backup.foldersync.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardwaresharing.backup.foldersync.FolderToSync;
import sneer.bricks.network.social.attributes.Attributes;

class FolderToSyncImpl implements FolderToSync {

	{
		my(Attributes.class).registerAttribute(FolderToSync.class);
	}

}
