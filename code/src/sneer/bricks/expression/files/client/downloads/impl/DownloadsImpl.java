package sneer.bricks.expression.files.client.downloads.impl;

import java.io.File;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;

class DownloadsImpl implements Downloads {

	@Override
	public Download newFileDownload(File file, long lastModified, Sneer1024 hashOfFile) {
		return new FileDownload(file, lastModified, hashOfFile);
	}

	@Override
	public Download newFileDownload(File file, long lastModified, Sneer1024 hashOfFile, Runnable toCallWhenFinished) {
		return new FileDownload(file, lastModified, hashOfFile, toCallWhenFinished);
	}

	@Override
	public Download newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFolder) {
		return new FolderDownload(folder, lastModified, hashOfFolder);
	}

	@Override
	public Download newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFolder, Runnable toCallWhenFinished) {
		return new FolderDownload(folder, lastModified, hashOfFolder, toCallWhenFinished);
	}

}
