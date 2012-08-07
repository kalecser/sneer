package sneer.bricks.expression.files.client.downloads.impl;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;

class DownloadsImpl implements Downloads {

	@Override
	public Download newFileDownload(File file, long size, long lastModified, Hash hashOfFile, Seal source) {
		return new FileDownload(file, size, lastModified, hashOfFile, source, true);
	}

	@Override
	public Download newFolderDownload(File folder, Hash hashOfFolder, Seal source, boolean copyLocalFiles) {
		return new FolderDownload(folder, hashOfFolder, source, copyLocalFiles);
	}

}
