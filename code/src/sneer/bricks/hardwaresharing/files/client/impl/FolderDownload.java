package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.brickness.Tuple;
import sneer.foundation.lang.Consumer;

class FolderDownload extends AbstractDownload {

	@SuppressWarnings("unused") private WeakContract _folderContentConsumerContract;
	private boolean _received = false;

	
	FolderDownload(File folder, long lastModified, Sneer1024 hashOfFolder) {
		super(folder, lastModified, hashOfFolder);

		subscribeToFolderContents();
		startSendingRequests();
	}

	
	private void subscribeToFolderContents() {
		_folderContentConsumerContract = my(TupleSpace.class).addSubscription(FolderContents.class, new Consumer<FolderContents>() { @Override public void consume(FolderContents folderContents) {
			receiveFolder(folderContents);
		}});
	}

	
	synchronized
	private void receiveFolder(FolderContents contents) {
		try {
			tryToReceiveFolder(contents);
		} catch (IOException ioe) {
			finishWith(ioe);
		}
	}

	
	private void tryToReceiveFolder(FolderContents folderContents) throws IOException {
		if (isFinished()) return;
		
	    Sneer1024 hashOfFolder = my(Hasher.class).hash(folderContents);
	    if (!_hash.equals(hashOfFolder)) return;
	    _received = true;
	    
	    if (!_path.exists() && !_path.mkdir()) throw new IOException("Unable to create folder: " + _path);

	    for (FileOrFolder entry : folderContents.contents)
	    	startDownload(entry).waitTillFinished();

	    if (_lastModified != -1)
	      _path.setLastModified(_lastModified);

	    finishWith(_path);
	}

	
	private Download startDownload(FileOrFolder entry) {
		return entry.isFolder
			? new FolderDownload(new File(_path, entry.name), entry.lastModified, entry.hashOfContents)
	    	  : new FileDownload(new File(_path, entry.name), entry.lastModified, entry.hashOfContents);	
	}

	
	@Override
	Tuple requestToPublishIfNecessary() {
		return _received
			? null
			: new FileRequest(_hash, 0, _path.getAbsolutePath());
	}

}
