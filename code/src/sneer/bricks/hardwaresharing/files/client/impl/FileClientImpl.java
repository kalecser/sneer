package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.protocol.BigFileBlocks;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.exceptions.NotImplementedYet;

class FileClientImpl implements FileClient {

	private final Object _downloadMonitor = new Object();
	private final CacheMap<Sneer1024, List<Download>> _downloadsByHash = CacheMap.newInstance();
	
	@SuppressWarnings("unused") private final WeakContract _fileContract;
	@SuppressWarnings("unused") private final WeakContract _folderContract;
	@SuppressWarnings("unused") private WeakContract _bigFileBlockContract;

	
	{
		_bigFileBlockContract = my(TupleSpace.class).addSubscription(BigFileBlocks.class, new Consumer<BigFileBlocks>() { @Override public void consume(BigFileBlocks contents) {
			receiveBigFileBlocks(contents);
		}});
		
		_fileContract = my(TupleSpace.class).addSubscription(FileContents.class, new Consumer<FileContents>() { @Override public void consume(FileContents contents) {
			receiveFile(contents);
		}});
		
		_folderContract = my(TupleSpace.class).addSubscription(FolderContents.class, new Consumer<FolderContents>() { @Override public void consume(FolderContents contents) {
			receiveFolder(contents);
		}});
	}

	
	@Override
	public void fetch(File file, Sneer1024 hashOfContents) throws IOException {
		fetch(file, -1, hashOfContents);
	}

	
	@Override
	public void fetch(File fileOrFolder, long lastModified, Sneer1024 hashOfContents) throws IOException {
		my(Logger.class).log("Fetching file or folder: {} hash:", fileOrFolder, hashOfContents);
		
		Download download;
		
		synchronized (_downloadMonitor) {
			download = new Download(fileOrFolder, lastModified, hashOfContents);
			if (download.isFinished()) {
				recurseIfFolder(fileOrFolder, lastModified, hashOfContents);
				return;
			}
			
			keepTrackOf(hashOfContents, download);
			FileRequestPublisher.startRequesting(hashOfContents, fileOrFolder.getName());
		}
		
		download.waitTillFinished();
		FileRequestPublisher.stopRequesting(hashOfContents);
		
		recurseIfFolder(fileOrFolder, lastModified, hashOfContents);
	}


	private void keepTrackOf(Sneer1024 hashOfContents, Download download) {
		List<Download> downloadsForTheSameHash = _downloadsByHash.get(hashOfContents, new Producer<List<Download>>() { @Override public List<Download> produce() throws RuntimeException {
			return new ArrayList<Download>();
		}});
		downloadsForTheSameHash.add(download);
	}


	private void recurseIfFolder(File fileOrFolder, long lastModified, Sneer1024 hashOfContents) throws IOException {
		Object contents = FileClientUtils.mappedContentsBy(hashOfContents);
		if (!(contents instanceof FolderContents)) return;
		
		if (!fileOrFolder.exists() && !fileOrFolder.mkdir()) throw new IOException("Unable to create folder: " + fileOrFolder);
		for (FileOrFolder entry : ((FolderContents)contents).contents)
			fetch(new File(fileOrFolder, entry.name), entry.lastModified, entry.hashOfContents);
		
		if (lastModified != -1)
			fileOrFolder.setLastModified(lastModified);
	}
	

	private void receiveFile(FileContents contents) {
		byte[] data = contents.bytes.copy();
		if (data == null) throw new IllegalArgumentException();
		Sneer1024 hash = my(Hasher.class).hash(data);
		finishDownloads(hash, data);
		my(Logger.class).log("file received", hash);
	}


	private void receiveFolder(FolderContents contents) {
		Sneer1024 hash = my(Hasher.class).hash(contents);
		finishDownloads(hash, contents);
		my(Logger.class).log("folder received", hash);
	}
	
	
	private void finishDownloads(Sneer1024 hash, Object data) {
		synchronized (_downloadMonitor) {
			List<Download> fulfilled = _downloadsByHash.remove(hash);
			if (fulfilled == null) {
				my(Logger.class).log("File Contents received and not used: ", hash);
				return;
			}
			
			for (Download download : fulfilled)
				download.finish(data);
		}
	}
	
	
	protected void receiveBigFileBlocks(@SuppressWarnings("unused") BigFileBlocks contents) {
//		my(FileMap.class).putBigFileBlocks(contents);
		throw new NotImplementedYet();
	}


}
