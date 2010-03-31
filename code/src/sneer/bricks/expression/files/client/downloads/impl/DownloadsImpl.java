package sneer.bricks.expression.files.client.downloads.impl;

import java.io.File;
import java.lang.ref.WeakReference;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.network.social.Contact;

class DownloadsImpl implements Downloads {

	@Override
	public WeakReference<Download> newFileDownload(File file, long lastModified, Sneer1024 hashOfFile) {
		return new WeakReference<Download>(new FileDownload(file, lastModified, hashOfFile));
	}

	@Override
	public WeakReference<Download> newFileDownload(File file, long lastModified, Sneer1024 hashOfFile, Contact source, Runnable toCallWhenFinished) {
		return new WeakReference<Download>(new FileDownload(file, lastModified, hashOfFile, source, toCallWhenFinished));
	}

	@Override
	public WeakReference<Download> newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFolder) {
		return new WeakReference<Download>(new FolderDownload(folder, lastModified, hashOfFolder));
	}

	@Override
	public WeakReference<Download> newFolderDownload(File folder, long lastModified, Sneer1024 hashOfFolder, Runnable toCallWhenFinished) {
		return new WeakReference<Download>(new FolderDownload(folder, lastModified, hashOfFolder, toCallWhenFinished));
	}

}
