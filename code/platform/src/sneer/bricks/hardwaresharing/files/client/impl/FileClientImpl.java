package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.BigFileBlocks;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Consumer;

class FileClientImpl implements FileClient {
	
	private final CacheMap<Sneer1024, Latch> _latchesByHash = CacheMap.newInstance();

	@SuppressWarnings("unused") private final WeakContract _fileContract;
	@SuppressWarnings("unused") private final WeakContract _folderContract;
	@SuppressWarnings("unused") private final WeakContract _cacheContract;
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
		
		_cacheContract = my(FileMap.class).contentsAdded().addReceiver(new Consumer<Sneer1024>() { @Override public void consume(Sneer1024 hashOfContents) {
			contentsReceived(hashOfContents);
		}});
	}

	
	@Override
	public void fetchToCache(Sneer1024 hashOfContents) {
		Latch latch;
		synchronized (this) {
			if (cachedContentsBy(hashOfContents) != null) return;
			FileRequestPublisher.startRequesting(hashOfContents);
			latch = _latchesByHash.get(hashOfContents, my(Latches.class));
		}
		
		latch.waitTillOpen();
		FileRequestPublisher.stopRequesting(hashOfContents);
		
		recurseIfFolder(hashOfContents);
	}


	private void recurseIfFolder(Sneer1024 hashOfContents) {
		Object contents = cachedContentsBy(hashOfContents);
		if (contents instanceof FolderContents)
			for (FileOrFolder entry : ((FolderContents)contents).contents)
				fetchToCache(entry.hashOfContents);
	}

	private Object cachedContentsBy(Sneer1024 hashOfContents) {
		return my(FileMap.class).getContents(hashOfContents);
	}
	
	synchronized
	private void contentsReceived(Sneer1024 hashOfContents) {
		Latch latch = _latchesByHash.remove(hashOfContents);
		if (latch != null) latch.open();
	}
	

	private void receiveFile(FileContents contents) {
		my(FileMap.class).putFileContents(contents.bytes.copy());
	}

	
	private void receiveFolder(FolderContents contents) {
		my(FileMap.class).putFolderContents(contents);
	}
	
	protected void receiveBigFileBlocks(BigFileBlocks contents) {
		my(FileMap.class).putBigFileBlocks(contents);
	}
}
