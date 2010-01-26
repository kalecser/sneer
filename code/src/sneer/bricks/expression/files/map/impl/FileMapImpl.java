package sneer.bricks.expression.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.io.log.Logger;

class FileMapImpl implements FileMap {

	private final Map<Sneer1024, File>				_filesByHash				= new ConcurrentHashMap<Sneer1024, File>();
	private final Map<File, Sneer1024>				_hashesByFile				= new ConcurrentHashMap<File, Sneer1024>();
	private final Map<File, Long>					_lastModifiedDatesByFile	= new ConcurrentHashMap<File, Long>();

	private final Map<Sneer1024, FolderContents>	_folderContentsByHash		= new ConcurrentHashMap<Sneer1024, FolderContents>();

	@Override
	synchronized
	public void putFile(File file, Sneer1024 hash) {
		this.putFile(file, file.lastModified(), hash);
	}

	@Override
	synchronized
	public void putFile(File file, long lastModified, Sneer1024 hash) {
		if (file.isDirectory()) throw new IllegalArgumentException("Parameter 'file' cannot be a directory");

		my(Logger.class).log("Mapping " + file + fileSizeInKB(file));
		_filesByHash.put(hash, file);
		_hashesByFile.put(file, hash);
		_lastModifiedDatesByFile.put(file, lastModified);
	}

	private String fileSizeInKB(File fileOrFolder) {
		return fileOrFolder.isDirectory() ? "" : "(" + fileOrFolder.length() / 1024 + " KB)";
	}

	@Override
	synchronized
	public File getFile(Sneer1024 hash) {
		return _folderContentsByHash.get(hash) != null ? null : _filesByHash.get(hash);
	}

	@Override
	public Sneer1024 getHash(File file) {
		return _hashesByFile.get(file);
	}

	@Override
	public long getLastModified(File file) {
		final Long result = _lastModifiedDatesByFile.get(file);
		return (result == null) ? -1 : result;
	}

	@Override
	synchronized
	public void putFolderContents(File folder, FolderContents contents, Sneer1024 hash) {
		_folderContentsByHash.put(hash, contents); 
		_filesByHash.put(hash, folder);
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
		final String pathToBeRemoved = folderToBeRemoved.getAbsolutePath();
		
		Iterator<Entry<Sneer1024, File>> entries = _filesByHash.entrySet().iterator();
		while (entries.hasNext()) {
			final Entry<Sneer1024, File> hashAndFile = entries.next();
			if (!hashAndFile.getValue().getAbsolutePath().startsWith(pathToBeRemoved))
				continue;
			
			entries.remove();
			_folderContentsByHash.remove(hashAndFile.getKey());
		}
	}

}
