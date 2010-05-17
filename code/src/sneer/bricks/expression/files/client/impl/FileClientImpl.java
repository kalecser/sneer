package sneer.bricks.expression.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

class FileClientImpl implements FileClient {

	private final Map<Hash, WeakReference<Download>> _downloadsByHash = new HashMap<Hash, WeakReference<Download>>();

	@Override
	public Download startFileDownload(File file, Hash hashOfFile) {
		return startFileDownload(file, -1, hashOfFile, null);
	}


	@Override
	public Download startFileDownload(final File file, final long lastModified, final Hash hashOfFile, final Seal source) {
		return startDownload(hashOfFile, new Producer<Download>() { @Override public Download produce() {
			return my(Downloads.class).newFileDownload(file, lastModified, hashOfFile, source, downloadCleaner(hashOfFile));
		}});
	}


	@Override
	public Download startFolderDownload(File folder, Hash hashOfFolder) {
		return startFolderDownload(folder, -1, hashOfFolder);
	}


	@Override
	public Download startFolderDownload(final File folder, final long lastModified, final Hash hashOfFolder) {
		return startDownload(hashOfFolder, new Producer<Download>() { @Override public Download produce() {
			return my(Downloads.class).newFolderDownload(folder, lastModified, hashOfFolder, downloadCleaner(hashOfFolder));
		}});
	}


	private Download startDownload(final Hash hash, Producer<Download> downloadFactory) {
		Download download;

		WeakReference<Download> downloadRef = downloadBy(hash);
		if (downloadRef != null) {
			download = downloadRef.get();
			if (download == null) {
				downloadCleaner(hash).run();
				return null;
			}
			return download;
		}

		download = downloadFactory.produce();
		synchronized (_downloadsByHash) {
			_downloadsByHash.put(hash, new WeakReference<Download>(download));
		}

		return download;
	}


	synchronized
	private WeakReference<Download> downloadBy(final Hash hash) {
		return _downloadsByHash.get(hash);
	}


	private Runnable downloadCleaner(final Hash hash) { 
		return new Closure() { @Override public void run() {
			synchronized (_downloadsByHash) {
				_downloadsByHash.remove(hash);				
			}
		}};
	}

}
