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
import sneer.bricks.expression.files.writer.folder.FolderContentsWriter;
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
	void subscribeToContents() {
		_folderContentConsumerContract = my(RemoteTuples.class).addSubscription(FolderContents.class, new Consumer<FolderContents>() { @Override public void consume(FolderContents folderContents) {
			receiveFolder(folderContents);
		}});
	}

	
	synchronized
	private void receiveFolder(FolderContents contents) {
		registerActivity();

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
	void updateFileMapWith(File tmpFolder, File actualFolder) {
		my(FileMap.class).putFolder(tmpFolder.getAbsolutePath(), _hash);
		my(FileMap.class).rename(tmpFolder.getAbsolutePath(), actualFolder.getAbsolutePath());
	}


	@Override
	Tuple requestToPublishIfNecessary() {
		return _contentsReceived != null
			? null
			: new FileRequest(source(), _hash, 0, _path.getAbsolutePath());
	}


	@Override
	void copyContents(Object contents) throws IOException {
		if (!(contents instanceof FolderContents)) throw new IOException("Wrong type of contents received. Should be FolderContents but was " + contents.getClass());
		_contentsReceived = (FolderContents) contents;
		my(FolderContentsWriter.class).writeToFolder(_path, _contentsReceived);
	}


	@Override
	Object mappedContentsBy(Hash hashOfContents) {
		return my(FileMap.class).getFolderContents(hashOfContents);
	}


}
