package sneer.bricks.expression.files.client.downloads.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import sneer.bricks.identity.seals.Seal;
import basis.lang.Consumer;
import basis.lang.arrays.ImmutableArray;

class FolderDownload extends AbstractDownload {

	private FolderContents _contentsReceived = null;

	@SuppressWarnings("unused") private WeakContract _folderContentConsumerContract;

	FolderDownload(File folder, Hash hashOfFolder, Seal source, boolean copyLocalFiles) {
		super(folder, -1, hashOfFolder, source, copyLocalFiles);
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
		? new FolderDownload(new File(_path, entry.name), entry.hashOfContents, _source, _copyLocalFiles)
		: new FileDownload(new File(_path, entry.name), entry.lastModified, entry.hashOfContents, _source, _copyLocalFiles);	
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
			: new FileRequest(source(), _hash, new ImmutableArray<>(0), _path.getAbsolutePath());
	}


	@Override
	protected void finishWithLocalContents(Object contents) throws IOException, TimeoutException {
		finishWith((FolderContents) contents);
	}


	@Override
	protected FolderContents mappedContentsBy(Hash hashOfContents) {
		return my(FileMap.class).getFolderContents(hashOfContents);
	}


	@Override
	protected boolean isWaitingForActivity() {
		return _contentsReceived == null;
	}


	@Override
	protected String getMappedPath(Hash hash) {
		List<String> folders = my(FileMap.class).getFolders(hash);
		return folders.isEmpty() ? null : folders.get(0);
	}


}
