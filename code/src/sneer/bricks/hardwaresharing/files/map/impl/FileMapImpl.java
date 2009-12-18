package sneer.bricks.hardwaresharing.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;

class FileMapImpl implements FileMap {

	private final Map<Sneer1024, File>           _filesByHash			= new ConcurrentHashMap<Sneer1024, File>();
	private final Map<Sneer1024, FolderContents> _folderContentsByHash	= new ConcurrentHashMap<Sneer1024, FolderContents>();
	private final Map<File, Sneer1024> _hashesByFolder 					= new ConcurrentHashMap<File, Sneer1024>();

	@Override
	synchronized
	public void putFolderContents(File folder, FolderContents contents, Sneer1024 hash) {
		_hashesByFolder.put(folder, hash);
		_folderContentsByHash.put(hash, contents); 
	}

	@Override
	synchronized
	public void put(File file, Sneer1024 hash) {
		transientPut(file, hash);
	}

	private void transientPut(File file, Sneer1024 hash) {
		my(Logger.class).log("Mapping " + file + fileSizeInKB(file));
		_filesByHash.put(hash, file);
	}

	private String fileSizeInKB(File fileOrFolder) {
		return fileOrFolder.isDirectory() ? "" : "(" + fileOrFolder.length() / 1024 + " KB)";
	}

	@Override
	synchronized
	public void remove(File fileOrFolderToBeRemoved) {
		if (fileOrFolderToBeRemoved.isDirectory()) {
			removeFolder(fileOrFolderToBeRemoved);
		} else {
			removeFile(fileOrFolderToBeRemoved);
		}
	}

	private void removeFolder(File folderToBeRemoved) {
		Iterator<Entry<Sneer1024, File>> filesByHashIterator = _filesByHash.entrySet().iterator();
		while (filesByHashIterator.hasNext()) {
			if (filesByHashIterator.next().getValue().getAbsolutePath().startsWith(folderToBeRemoved.getAbsolutePath()))
				filesByHashIterator.remove();
		}

		Iterator<Entry<File, Sneer1024>> hashesByFolderIterator = _hashesByFolder.entrySet().iterator();
		while (hashesByFolderIterator.hasNext()) {
			Entry<File, Sneer1024> hashByFolder = hashesByFolderIterator.next();
			if (hashByFolder.getKey().getAbsolutePath().startsWith(folderToBeRemoved.getAbsolutePath())) {
				_folderContentsByHash.remove(hashByFolder.getValue());
				filesByHashIterator.remove();
			}
		}
	}

	private void removeFile(File fileToBeRemoved) {
		_filesByHash.remove(fileToBeRemoved);
	}

	@Override
	synchronized
	public File getFile(Sneer1024 hash) {
		return _filesByHash.get(hash);
	}

	@Override
	synchronized
	public FolderContents getFolder(Sneer1024 hash) {
		return _folderContentsByHash.get(hash);
	}

}
