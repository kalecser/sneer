package sneer.bricks.expression.files.client.downloads.impl;

import java.io.File;
import java.lang.ref.WeakReference;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;

class DownloadsImpl implements Downloads {

	@Override
	public WeakReference<Download> newFileDownload(File file, long lastModified, Hash hashOfFile) {
		return new WeakReference<Download>(new FileDownload(file, lastModified, hashOfFile));
	}

	@Override
	public WeakReference<Download> newFileDownload(File file, long lastModified, Hash hashOfFile, Seal source, Runnable toCallWhenFinished) {
		return new WeakReference<Download>(new FileDownload(file, lastModified, hashOfFile, source, toCallWhenFinished));
	}

	@Override
	public WeakReference<Download> newFolderDownload(File folder, long lastModified, Hash hashOfFolder) {
		return new WeakReference<Download>(new FolderDownload(folder, lastModified, hashOfFolder));
	}

	@Override
	public WeakReference<Download> newFolderDownload(File folder, long lastModified, Hash hashOfFolder, Runnable toCallWhenFinished) {
		return new WeakReference<Download>(new FolderDownload(folder, lastModified, hashOfFolder, toCallWhenFinished));
	}

}
