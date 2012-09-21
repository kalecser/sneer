package sneer.bricks.expression.files.client.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.Downloads;
import sneer.bricks.expression.files.client.downloads.old.DownloadsOld;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.computers.channels.Channels;
import basis.lang.Closure;
import basis.lang.Producer;

class FileClientImpl implements FileClient {

	private final Map<Hash, WeakReference<Download>> _downloadsByHash = new HashMap<Hash, WeakReference<Download>>();
	private final Downloads business = Channels.READY_FOR_PRODUCTION ? my(Downloads.class) : my(DownloadsOld.class);
	
	@Override
	public Download startDownload(File file, boolean isFolder, long size, long lastModified, Hash hashOfFile, Seal source) {
		return isFolder
			? startFolderDownload(file, hashOfFile, source)
			: startFileDownload(file, size, lastModified, hashOfFile, source);
	}

	
	@Override
	public Download startFileDownload(final File file, final long size, final long lastModified, final Hash hashOfFile, final Seal source) {
		return startDownload(hashOfFile, new Producer<Download>() { @Override public Download produce() {
			return cleaningOnFinished(business.newFileDownload(file, size, lastModified, hashOfFile, source), hashOfFile);
		}});
	}

	@Override
	public Download startFolderDownload(File folder, Hash hashOfFolder, Seal source) {
		return startFolderDownload(folder, hashOfFolder, source, true);
	}

	@Override
	public Download startFolderDeltasDownload(File folder, Hash hashOfFolder, Seal source) {
		return startFolderDownload(folder, hashOfFolder, source, false);
	}
	
	private Download startFolderDownload(final File folder, final Hash hashOfFolder, final Seal source, final boolean copyLocalFiles) {
		return startDownload(hashOfFolder, new Producer<Download>() { @Override public Download produce() {
			return cleaningOnFinished(business.newFolderDownload(folder, hashOfFolder, source, copyLocalFiles), hashOfFolder);
		}});
	}

	private Download startDownload(final Hash hash, Producer<Download> downloadFactory) {
		Download result;

		synchronized (_downloadsByHash) {
			WeakReference<Download> weakRef = _downloadsByHash.get(hash);

			if (weakRef != null) {
				result = weakRef.get();
				if (result != null)
					return result;
			}

			result = downloadFactory.produce();
			_downloadsByHash.put(hash, new WeakReference<Download>(result));
		}

		return result;
	}


	private Closure downloadCleaner(final Hash hash) { 
		return new Closure() { @Override public void run() {
			synchronized (_downloadsByHash) {
				_downloadsByHash.remove(hash);				
			}
		}};
	}

	private Download cleaningOnFinished(Download download, final Hash hashOfFile) {
		download.onFinished(downloadCleaner(hashOfFile));
		return download;
	}
}
