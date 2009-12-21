package sneer.bricks.expression.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.io.log.Logger;

class FileMapImpl implements FileMap {

	private final Map<Sneer1024, File>           	_filesByHash			= new ConcurrentHashMap<Sneer1024, File>();
	private final Map<Sneer1024, FolderContents>	_folderContentsByHash	= new ConcurrentHashMap<Sneer1024, FolderContents>();
	private final Map<File, Sneer1024>				_hashesByFolder			= new ConcurrentHashMap<File, Sneer1024>();

	@Override
	synchronized
	public void putFile(File file, Sneer1024 hash) {
		if (file.isDirectory()) throw new IllegalArgumentException("Parameter 'file' cannot be a directory");
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
	public File getFile(Sneer1024 hash) {
		return _filesByHash.get(hash);
	}

	@Override
	synchronized
	public void putFolderContents(File folder, FolderContents contents, Sneer1024 hash) {
		_hashesByFolder.put(folder, hash);
		_folderContentsByHash.put(hash, contents); 
	}

	@Override
	synchronized
	public FolderContents getFolderContents(Sneer1024 hash) {
		return _folderContentsByHash.get(hash);
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

	private void removeFile(File fileToBeRemoved) {
		Iterator<File> filesInTheMap = _filesByHash.values().iterator();
		while (filesInTheMap.hasNext()) {
			if (filesInTheMap.next().equals(fileToBeRemoved)) {
				filesInTheMap.remove();
				break;
			}
		}
	}

	private void removeFolder(File folderToBeRemoved) {
		Iterator<File> filesInTheMap = _filesByHash.values().iterator();
		while (filesInTheMap.hasNext()) {
			if (filesInTheMap.next().getAbsolutePath().startsWith(folderToBeRemoved.getAbsolutePath()))
				filesInTheMap.remove();
		}

		Iterator<Entry<File, Sneer1024>> hashesByFolder = _hashesByFolder.entrySet().iterator();
		while (hashesByFolder.hasNext()) {
			Entry<File, Sneer1024> hashByFolder = hashesByFolder.next();
			if (hashByFolder.getKey().getAbsolutePath().startsWith(folderToBeRemoved.getAbsolutePath())) {
				hashesByFolder.remove();
				_folderContentsByHash.remove(hashByFolder.getValue());
			}
		}
	}

}
