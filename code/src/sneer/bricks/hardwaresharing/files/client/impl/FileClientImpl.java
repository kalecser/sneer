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
	public void fetch(File file, Sneer1024 hashOfContents) throws IOException {
		fetch(file, -1, hashOfContents);
	}


	@Override
	public void fetch(final File fileOrFolder, final long lastModified, final Sneer1024 hashOfContents) throws IOException {
		my(Logger.class).log("Fetching file or folder: {} hash:", fileOrFolder, hashOfContents);

		Download download = _downloadsByHash.get(hashOfContents, new Producer<Download>() { @Override public Download produce() throws RuntimeException {
			return new Download(fileOrFolder, lastModified, hashOfContents); 
		}});

		download.waitTillFinished();
		_downloadsByHash.remove(hashOfContents);
	}

}
