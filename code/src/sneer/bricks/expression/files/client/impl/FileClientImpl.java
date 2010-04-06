package sneer.bricks.expression.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.lang.ref.WeakReference;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.network.social.Contact;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

class FileClientImpl implements FileClient {

	private final CacheMap<Hash, WeakReference<Download>> _downloadsByHash = CacheMap.newInstance();

	@Override
	public Download startFileDownload(File file, Hash hashOfFile) {
		return startFileDownload(file, -1, hashOfFile, null);
	}


	@Override
	public Download startFileDownload(final File file, final long lastModified, final Hash hashOfFile, final Contact source) {
		return _downloadsByHash.get(hashOfFile, new Producer<WeakReference<Download>>() { @Override public WeakReference<Download> produce() throws RuntimeException {
			return my(Downloads.class).newFileDownload(file, lastModified, hashOfFile, source, downloadCleaner(hashOfFile));
		}}).get();
	}


	@Override
	public Download startFolderDownload(File folder, Hash hashOfFolder) {
		return startFolderDownload(folder, -1, hashOfFolder);
	}


	@Override
	public Download startFolderDownload(final File folder, final long lastModified, final Hash hashOfFolder) {
		return _downloadsByHash.get(hashOfFolder, new Producer<WeakReference<Download>>() { @Override public WeakReference<Download> produce() throws RuntimeException {
			return my(Downloads.class).newFolderDownload(folder, lastModified, hashOfFolder, downloadCleaner(hashOfFolder));
		}}).get();
	}


	private Runnable downloadCleaner(final Hash hash) { 
		return new Closure() { @Override public void run() {
			_downloadsByHash.remove(hash);
		}};
	}

}
