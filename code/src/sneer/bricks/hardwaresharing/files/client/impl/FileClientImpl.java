package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class FileClientImpl implements FileClient {

	private final CacheMap<Sneer1024, Download> _downloadsByHash = CacheMap.newInstance();


	@Override
	public void fetchFile(File file, Sneer1024 hashOfFile) throws IOException {
		fetchFile(file, -1, hashOfFile);
	}


	@Override
	public void fetchFile(final File file, final long lastModified, final Sneer1024 hashOfFile) throws IOException {
		fetch("file", file, hashOfFile, new Producer<Download>() { @Override public Download produce() throws RuntimeException {
			return new FileDownload(file, lastModified, hashOfFile); 
		}});
	}


	@Override
	public void fetchFolder(File folder, Sneer1024 hashOfFolder) throws IOException {
		fetchFolder(folder, -1, hashOfFolder);
	}


	@Override
	public void fetchFolder(final File folder, final long lastModified, final Sneer1024 hashOfFolder) throws IOException {
		fetch("folder", folder, hashOfFolder, new Producer<Download>() { @Override public Download produce() throws RuntimeException {
			return new FolderDownload(folder, lastModified, hashOfFolder); 
		}});
	}


	private void fetch(String type, File fileOrFolder, Sneer1024 hash, Producer<Download> downloadProducer) throws IOException {
		my(Logger.class).log("Fetching " + type + ": {} hash:", fileOrFolder, hash);

		Download download = _downloadsByHash.get(hash, downloadProducer);

		download.waitTillFinished();
		_downloadsByHash.remove(hash);
	}


}
