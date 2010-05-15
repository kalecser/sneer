package sneer.bricks.expression.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Predicate;

class FileMapImpl implements FileMap {

	private final Map<Hash, File> _filesByHash				= new ConcurrentHashMap<Hash, File>();
	private final Map<File, Hash> _hashesByFile				= new ConcurrentHashMap<File, Hash>();
	private final Map<File, Long> _lastModifiedDatesByFile	= new ConcurrentHashMap<File, Long>();

	private final Map<Hash, FolderContents>	_folderContentsByHash = new ConcurrentHashMap<Hash, FolderContents>();

	@Override
	public void putFile(File file, long lastModified, Hash hash) {
		if (isFolder(file)) throw new IllegalArgumentException("Parameter 'file' cannot be a directory: " + file.getAbsolutePath());

		my(Logger.class).log("Mapping {} ({} KB)", file, fileSizeInKB(file));
		_filesByHash.put(hash, file);
		_hashesByFile.put(file, hash);
		_lastModifiedDatesByFile.put(file, lastModified);
	}

	private long fileSizeInKB(File file) {
		return file.length() / 1024;
	}

	@Override
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
	public void putFolderContents(File folder, FolderContents contents, Hash hash) {
		my(Logger.class).log("Mapping {} Hash: ", folder, hash);
		_folderContentsByHash.put(hash, contents); 
		_filesByHash.put(hash, folder);
		_hashesByFile.put(folder, hash);
	}

	@Override
	public FolderContents getFolderContents(Hash hash) {
		return _folderContentsByHash.get(hash);
	}

	@Override
	public Hash remove(File fileOrFolder) {
		return isFolder(fileOrFolder) ? removeFolder(fileOrFolder) : removeFile(fileOrFolder);
	}

	private boolean isFolder(File fileOrFolder) {
//		return getFolderContents(getHash(fileOrFolder)) != null;
		return fileOrFolder.isDirectory();
	}

	private Hash removeFile(File fileToBeRemoved) {
		Hash hashOfFile = getHash(fileToBeRemoved);
		if (hashOfFile == null) return null;

		my(Logger.class).log("Unmapping " + fileToBeRemoved + fileSizeInKB(fileToBeRemoved));

		_filesByHash.remove(hashOfFile);
		_hashesByFile.remove(fileToBeRemoved);
		_lastModifiedDatesByFile.remove(fileToBeRemoved);

		return hashOfFile;
	}

	private Hash removeFolder(File folderToBeRemoved) {
		final Hash hashOfFolder = getHash(folderToBeRemoved); // It may be null if the folder's mapping didn't finish successfully
		_folderContentsByHash.remove(hashOfFolder);

		final String pathToBeRemoved = folderToBeRemoved.getAbsolutePath();
		loopFilesThatStartWithAndDo(pathToBeRemoved, new Consumer<File>() { @Override public void consume(File fileInTheMap) {
			removeFile(fileInTheMap);
		}});

		return hashOfFolder;
	}

	private void loopFilesThatStartWithAndDo(final String pathPrefix, Consumer<File> toDo) {
		loopFilesAndDo(toDo, new Predicate<File>() { @Override public boolean evaluate(File file) {
			return file.getAbsolutePath().startsWith(pathPrefix);
		}});
	}

	private void loopFilesAndDo(Consumer<File> toDo, Predicate<File> condition) {
		for (File fileInTheMap : _filesByHash.values().toArray(new File[0]))
			if (condition.evaluate(fileInTheMap))
				toDo.consume(fileInTheMap);
	}

	@Override
	public void rename(final File from, final File to) {
		final String fromPath = from.getAbsolutePath();
		loopFilesThatStartWithAndDo(fromPath, new Consumer<File>() { @Override public void consume(File fileInTheMap) {
			renameEntry(fileInTheMap, fromPath, to);			
		}});
	}

	private void renameEntry(File from, final String fromParent, File toParent) {
		String relativePath = my(Lang.class).strings().removeStart(from.getAbsolutePath(), fromParent);
		File to = new File(toParent, relativePath);
		renameEntry(from, to);
	}

	private void renameEntry(File from, File to) {
		Hash hash = getHash(from);
		my(Logger.class).log("Renaming from: {} to: ", from, to);
		if (isFolder(to)) {
			FolderContents folderContents = getFolderContents(hash);
			putFolderContents(to, folderContents, hash);
		} else {
			putFile(to, getLastModified(from), hash);
			_lastModifiedDatesByFile.remove(from);
		}
		_hashesByFile.remove(from);
	}

}
