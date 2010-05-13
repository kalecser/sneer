package sneer.bricks.expression.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.lang.ref.WeakReference;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.lang.ByRef;
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
	public Download startFileDownload(final File file, final long lastModified, final Hash hashOfFile, final Seal source) {
		final ByRef<Download> refToAvoidGc = ByRef.newInstance();
		return _downloadsByHash.get(hashOfFile, new Producer<WeakReference<Download>>() { @Override public WeakReference<Download> produce() throws RuntimeException {
			refToAvoidGc.value = my(Downloads.class).newFileDownload(file, lastModified, hashOfFile, source, downloadCleaner(hashOfFile));
			return new WeakReference<Download>(refToAvoidGc.value);
		}}).get();
	}


	@Override
	public Download startFolderDownload(File folder, Hash hashOfFolder) {
		return startFolderDownload(folder, -1, hashOfFolder);
	}


	@Override
	public Download startFolderDownload(final File folder, final long lastModified, final Hash hashOfFolder) {
		final ByRef<Download> refToAvoidGc = ByRef.newInstance();
		return _downloadsByHash.get(hashOfFolder, new Producer<WeakReference<Download>>() { @Override public WeakReference<Download> produce() throws RuntimeException {
			refToAvoidGc.value = my(Downloads.class).newFolderDownload(folder, lastModified, hashOfFolder, downloadCleaner(hashOfFolder));
			return new WeakReference<Download>(refToAvoidGc.value);
		}}).get();
	}


	private Runnable downloadCleaner(final Hash hash) { 
		return new Closure() { @Override public void run() {
			_downloadsByHash.remove(hash);
		}};
	}

}
