package sneer.bricks.expression.files.client.downloads.old.impl;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.old.DownloadsOld;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;

class DownloadsOldImpl implements DownloadsOld {

	@Override
	public Download newFileDownload(File file, long size, long lastModified, Hash hashOfFile, Seal source) {
		return new FileDownloadOld(file, size, lastModified, hashOfFile, source, true);
	}

	@Override
	public Download newFolderDownload(File folder, Hash hashOfFolder, Seal source, boolean copyLocalFiles) {
		return new FolderDownloadOld(folder, hashOfFolder, source, copyLocalFiles);
	}

}
