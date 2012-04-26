package sneer.bricks.expression.files.transfer.downloadfolder.impl;

import static basis.environments.Environments.my;
import sneer.bricks.expression.files.transfer.downloadfolder.DownloadFolder;
import sneer.bricks.network.social.attributes.Attributes;


class DownloadFolderImpl implements DownloadFolder {
	
	{
		my(Attributes.class).registerAttribute(DownloadFolder.class);
	}

}
