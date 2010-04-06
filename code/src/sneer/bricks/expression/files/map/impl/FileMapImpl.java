package sneer.bricks.expression.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.foundation.lang.Predicate;

class FileMapImpl implements FileMap {

	private final Map<Hash, File> _filesByHash				= new ConcurrentHashMap<Hash, File>();
	private final Map<File, Hash> _hashesByFile				= new ConcurrentHashMap<File, Hash>();
	private final Map<File, Long> _lastModifiedDatesByFile	= new ConcurrentHashMap<File, Long>();

	private final Map<Hash, FolderContents>	_folderContentsByHash		= new ConcurrentHashMap<Hash, FolderContents>();

	@Override
	synchronized
	public void putFile(File file, Hash hash) {
		this.putFile(file, file.lastModified(), hash);
	}

	@Override
	synchronized
	public void putFile(File file, long lastModified, Hash hash) {
		if (file.isDirectory()) throw new IllegalArgumentException("Parameter 'file' cannot be a directory: " + file.getAbsolutePath());

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
	public File getFile(Hash hash) {
		return _folderContentsByHash.get(hash) != null
		? null
		: _filesByHash.get(hash);
	}

	@Override
	public Hash getHash(File file) {
		return _hashesByFile.get(file);
	}

	@Override
	public long getLastModified(File file) {
		final Long result = _lastModifiedDatesByFile.get(file);
		return (result == null) ? -1 : result;
	}

	@Override
	synchronized
	public void putFolderContents(File folder, FolderContents contents, Hash hash) {
		_folderContentsByHash.put(hash, contents); 
		_filesByHash.put(hash, folder);
		_hashesByFile.put(folder, hash);
	}

	@Override
	public FolderContents getFolderContents(Hash hash) {
		return _folderContentsByHash.get(hash);
	}

	@Override
	synchronized
	public Hash remove(File fileOrFolderToBeRemoved) {
		Hash removed = getHash(fileOrFolderToBeRemoved);

		if (fileOrFolderToBeRemoved.isDirectory()) {
			removeFolder(fileOrFolderToBeRemoved);
		} else {
			removeFile(fileOrFolderToBeRemoved);
		}

		return removed;
	}

	private void removeFile(File fileToBeRemoved) {
		Iterator<File> filesInTheMap = _filesByHash.values().iterator();
		while (filesInTheMap.hasNext()) {
			if (filesInTheMap.next().equals(fileToBeRemoved)) {
				my(Logger.class).log("Unmapping " + fileToBeRemoved + fileSizeInKB(fileToBeRemoved));
				_hashesByFile.remove(fileToBeRemoved);
				_lastModifiedDatesByFile.remove(fileToBeRemoved);
				filesInTheMap.remove();
				break;
			}
		}
	}

	private void removeFolder(File folderToBeRemoved) {
		final String pathToBeRemoved = folderToBeRemoved.getAbsolutePath();
		
		Iterator<Entry<Hash, File>> entries = _filesByHash.entrySet().iterator();
		while (entries.hasNext()) {
			final Entry<Hash, File> hashAndFile = entries.next();
			if (!hashAndFile.getValue().getAbsolutePath().startsWith(pathToBeRemoved))
				continue;

			entries.remove();
			_folderContentsByHash.remove(hashAndFile.getKey());
		}
	}
	
	@Override
	synchronized
	public void rename(File from, File to) {
		final String fromPath =
			from.getAbsolutePath().endsWith(File.separator)
				? from.getAbsolutePath()
				: from.getAbsolutePath() + File.separator;

		Collection<File> filesToBeRenamed = my(CollectionUtils.class).filter(_filesByHash.values(), new Predicate<File>() { @Override public boolean evaluate(File candidate) {
			return (candidate.getAbsolutePath() + File.separator).startsWith(fromPath);
		}});

		for (File file : filesToBeRenamed)
			renameEntry(fromPath, to, file);
	}

	private void renameEntry(final String fromParent, File toParent, File file) {
		String relativePath = my(Lang.class).strings().removeStart(file.getAbsolutePath(), fromParent);
		File renamedFile = new File(toParent, relativePath);
		Hash hash = getHash(file);
		if(getFolderContents(hash) != null) {
			FolderContents folderContents = getFolderContents(hash);
			putFolderContents(renamedFile, folderContents, hash);
		}
		else {
			putFile(renamedFile, getLastModified(file), hash);
		}
		remove(file);
	}

}
