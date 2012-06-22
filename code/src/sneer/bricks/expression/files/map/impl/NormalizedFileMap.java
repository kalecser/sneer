package sneer.bricks.expression.files.map.impl;

import static basis.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.impl.FileMapData.Entry;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.Lang.Strings;
import sneer.bricks.hardware.io.log.Logger;


/**
 * Expects all path args to have unix-style separators "/" and no trailing separators.
 * 
 * IMPORTANT: Folders are represented with lastModifiedDate -1.
 **/
class NormalizedFileMap implements FileMap {

	private static final Strings Strings = my(Lang.class).strings();

	private final FileMapData _data = new FileMapData();

	@Override
	public void putFile(String file, long lastModified, Hash hash) {
		if (lastModified < 0) throw new IllegalArgumentException("File '" + file + "' cannot be mapped with lastModified date smaller than zero: " + lastModified);
		putPath(file, lastModified, hash, false);
	}


	@Override
	public void putFolder(String path, Hash hash) {
		putPath(path, -1, hash, true);
//		checkHash(path, hash);
	}


//	private void checkHash(String path, Hash expected) {
//		FolderContents folderContents = getFolderContents(expected);
//		Hash actual = my(FolderContentsHasher.class).hash(folderContents);
//		if(!expected.equals(actual)) {
//			String entries = "";
//			for (FileOrFolder entry : folderContents.contents)
//				entries += "\n" + entry.toString();
//			throw new IllegalArgumentException("HASH: expected " + expected + ", got " + actual + " for " + path + " entries: " + entries);
//		}
//	}

	
	private void putPath(String path, long lastModified, Hash hash, boolean isFolder) {
		my(Logger.class).log("Mapping ", path);
		_data.put(path, lastModified, hash, isFolder);
	}
	
	
	@Override
	public Hash getHash(String path) {
		return _data.getHash(path);
	}

	
	@Override
	public long getLastModified(String file) {
		Long result = _data.getLastModified(file);
		if (result == null) throw new IllegalArgumentException("File not found in map: " + file);
		if (result == -1) throw new IllegalArgumentException("Path mapped as a folder, not a file: " + file);
		return result;
	}

	
	@Override
	public FolderContents getFolderContents(Hash hash) {
		return new FolderContentsGetter(_data, hash).result();
	}


	@Override
	public Hash remove(String path) {
		return movePath(path, null);
	}


	@Override
	public void rename(String from, String to) {
		my(Logger.class).log("FileMap renaming {} to ", from, to);
		movePath(from, to);
	}
	
	
	private Hash movePath(String from, String to) {
		Hash result = getHash(from);

		if (result != null) {
			boolean isFolder = isFolder(from);
			replaceSinglePath(from, to);
			if (!isFolder)
				return result;
		}
		
		replacePrefixes(from, to);
		return result;
	}

	
	private Hash replaceSinglePath(String from, String to) {
		Entry entry = _data.remove(from);
		
		if (to != null)
			putPath(to, entry.lastModified, entry.hash, entry.isFolder);
		
		return entry.hash;
	}
	

	private void replacePrefixes(String from, String to) {
		from += "/";
		if (to != null) to += "/";
		
		for (String candidate : _data.allPaths())
			replacePrefix(candidate, from, to);
	}


	private void replacePrefix(String path, String prefix, String newPrefix) {
		if (!path.startsWith(prefix)) return;
		
		replaceSinglePath(path, newPath(path, prefix, newPrefix));
	}


	private String newPath(String path, String prefix, String newPrefix) {
		if (newPrefix == null) return null;
		String relativePath = Strings.removeStart(path, prefix);
		return newPrefix + relativePath;
	}

	
	private boolean isFolder(String path) {
		long lastModified = _data.getLastModified(path);
		return lastModified == -1;
	}


	@Override
	public List<String> getFiles(Hash hash) {
		return _data.getFiles(hash);
	}


	@Override
	public List<FileOrFolder> dir(String path) {
		ArrayList<FileOrFolder> contents = new ArrayList<FileOrFolder>();
		String folder = path + "/";			
		for (String candidate : _data.allPaths())
			FolderContentsGetter.accumulateChild(_data, candidate, folder, contents, false);
		return contents;
	}


	@Override
	public List<String> getFolders(Hash hash) {
		return _data.getFolders(hash);
	}

}
