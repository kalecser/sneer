package sneer.bricks.expression.files.client.downloads.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.hasher.FolderContentsHasher;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FileRequest;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;

class FolderDownload extends AbstractDownload {

	private FolderContents _contentsReceived = null;

	@SuppressWarnings("unused") private WeakContract _folderContentConsumerContract;


	FolderDownload(File folder, Hash hashOfFolder) {
		this(folder, hashOfFolder, null);
	}


	FolderDownload(File folder, Hash hashOfFolder, Runnable toCallWhenFinished) {
		super(folder, -1, hashOfFolder, null, toCallWhenFinished);

		start();
	}


	@Override
	protected void subscribeToContents() {
		_folderContentConsumerContract = my(RemoteTuples.class).addSubscription(FolderContents.class, new Consumer<FolderContents>() { @Override public void consume(FolderContents folderContents) {
			receiveFolder(folderContents);
		}});
	}

	
	synchronized
	private void receiveFolder(FolderContents contents) {
		try {
			tryToReceiveFolder(contents);
		} catch (Exception e) {
			finishWith(e);
		}
	}

	
	private void tryToReceiveFolder(FolderContents folderContents) throws IOException, TimeoutException {
		if (isFinished()) return;

	    Hash hashOfFolder = my(FolderContentsHasher.class).hash(folderContents);
	    if (!_hash.equals(hashOfFolder)) return;
	    
	    finishWith(folderContents);
	}


	private void finishWith(FolderContents folderContents) throws IOException,	TimeoutException {
		_contentsReceived = folderContents;

	    if (!_path.exists() && !_path.mkdir()) throw new IOException("Unable to create folder: " + _path);

	    for (FileOrFolder entry : folderContents.contents)
	    	startSpinOffDownload(entry).waitTillFinished();

	    finishWithSuccess();
	}


	private Download startSpinOffDownload(FileOrFolder entry) {
		return entry.isFolder
		? new FolderDownload(new File(_path, entry.name), entry.hashOfContents)
		: new FileDownload(new File(_path, entry.name), entry.lastModified, entry.hashOfContents);	
	}


	@Override
	protected void updateFileMap() {
		my(FileMap.class).putFolder(_path.getAbsolutePath(), _hash);
		my(FileMap.class).rename(_path.getAbsolutePath(), _actualPath.getAbsolutePath());
	}


	@Override
	protected Tuple requestToPublishIfNecessary() {
		return _contentsReceived != null
			? null
			: new FileRequest(source(), _hash, 0, _path.getAbsolutePath());
	}


	@Override
	protected void finishWithLocalContents(Object contents) throws IOException, TimeoutException {
		finishWith((FolderContents) contents);
	}


	@Override
	protected Object mappedContentsBy(Hash hashOfContents) {
		return my(FileMap.class).getFolderContents(hashOfContents);
	}


	@Override
	protected boolean isWaitingForActivity() {
		return _contentsReceived == null;
	}


}
