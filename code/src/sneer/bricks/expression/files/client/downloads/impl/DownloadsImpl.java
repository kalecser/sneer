package sneer.bricks.expression.files.client.downloads.impl;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;

class DownloadsImpl implements Downloads {

	@Override
	public Download newFileDownload(File file, long lastModified, Hash hashOfFile, Seal source, Runnable toCallWhenFinished) {
		return new FileDownload(file, lastModified, hashOfFile, source, toCallWhenFinished, true);
	}

	@Override
	public Download newFolderDownload(File folder, Hash hashOfFolder, Runnable toCallWhenFinished, boolean copyLocalFiles) {
		return new FolderDownload(folder, hashOfFolder, toCallWhenFinished, copyLocalFiles);
	}

}
