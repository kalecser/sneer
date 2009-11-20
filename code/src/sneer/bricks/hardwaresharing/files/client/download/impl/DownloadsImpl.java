package sneer.bricks.hardwaresharing.files.client.download.impl;

import java.io.File;

import sneer.bricks.hardwaresharing.files.client.download.Download;
import sneer.bricks.hardwaresharing.files.client.download.Downloads;
import sneer.bricks.pulp.crypto.Sneer1024;

class DownloadsImpl implements Downloads {

	@Override
	public Download newFileDownload(File file, long lastModified, Sneer1024 hashOfFile) {
		return new FileDownload(file, lastModified, hashOfFile);
	}

	@Override
	public Download newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFolder) {
		return new FolderDownload(folder, lastModified, hashOfFolder);
	}

}
