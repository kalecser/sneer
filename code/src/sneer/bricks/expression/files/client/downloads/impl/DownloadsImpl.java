package sneer.bricks.expression.files.client.downloads.impl;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;

class DownloadsImpl implements Downloads {

	@Override
	public Download newFileDownload(File file, long lastModified, Hash hashOfFile) {
		return new FileDownload(file, lastModified, hashOfFile);
	}

	@Override
	public Download newFileDownload(File file, long lastModified, Hash hashOfFile, Seal source, Runnable toCallWhenFinished) {
		return new FileDownload(file, lastModified, hashOfFile, source, toCallWhenFinished);
	}

	@Override
	public Download newFolderDownload(File folder, long lastModified, Hash hashOfFolder) {
		return new FolderDownload(folder, lastModified, hashOfFolder);
	}

	@Override
	public Download newFolderDownload(File folder, long lastModified, Hash hashOfFolder, Runnable toCallWhenFinished) {
		return new FolderDownload(folder, lastModified, hashOfFolder, toCallWhenFinished);
	}

}
