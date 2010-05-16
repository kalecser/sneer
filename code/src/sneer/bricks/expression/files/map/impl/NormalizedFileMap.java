package sneer.bricks.expression.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.Lang.Strings;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.arrays.ImmutableArray;


/**
 * Expects all path args to have unix-style separators "/" and no trailing separators.
 * 
 * IMPORTANT: Folders are represented with lastModifiedDate -1.
 *
 *   
 *   
 **/
class NormalizedFileMap implements FileMap {

	private static final Strings Strings = my(Lang.class).strings();

	
	private final Map<Hash, String> _pathsByHash			 = new ConcurrentHashMap<Hash, String>();
	private final Map<String, Hash> _hashesByPath			 = new ConcurrentHashMap<String, Hash>();
	private final Map<String, Long> _lastModifiedDatesByPath = new ConcurrentHashMap<String, Long>();


	@Override
	public void putFile(String file, long lastModified, Hash hash) {
		if (lastModified < 0) throw new IllegalArgumentException("File '" + file + "' cannot be mapped with lastModified date smaller than zero: " + lastModified);
		putPath(file, lastModified, hash);
	}


	@Override
	public void putFolder(String path, Hash hash) {
		putPath(path, -1, hash);
	}

	
	private void putPath(String path, long lastModified, Hash hash) {
		my(Logger.class).log("Mapping", path);
		_pathsByHash.put(hash, path);
		_hashesByPath.put(path, hash);
		_lastModifiedDatesByPath.put(path, lastModified);
	}
	
	
	@Override
	public String getFile(Hash hash) {
		String path = _pathsByHash.get(hash);
		if (path == null) return null;
		return isFolder(path)
			? null
			: path;
	}

	
	@Override
	public Hash getHash(String path) {
		return _hashesByPath.get(path);
	}

	
	@Override
	public long getLastModified(String file) {
		Long result = _lastModifiedDatesByPath.get(file);
		if (result == null) throw new IllegalArgumentException("File not found in map: " + file);
		if (result == -1) throw new IllegalArgumentException("Path mapped as a folder, not a file: " + file);
		return result;
	}

	
	@Override
	public FolderContents getFolderContents(Hash hash) {
		String path = _pathsByHash.get(hash);
		if (path == null) return null;
		if (!isFolder(path)) return null;

		String folder = path + "/";
		
		List<FileOrFolder> contents = new ArrayList<FileOrFolder>();
		for (String candidate : allPaths())
			accumulateDirectChildren(candidate, folder, contents);
		
		Collections.sort(contents, new Comparator<FileOrFolder>() { @Override public int compare(FileOrFolder f1, FileOrFolder f2) {
			return f1.name.compareTo(f2.name);
		}});
		
//		FolderContents result = new FolderContents(new ImmutableArray<FileOrFolder>(contents));
//		if (!my(FolderContentsHasher.class).hash(result).equals(hash)) throw new IllegalStateException();
//		return result;

		return new FolderContents(new ImmutableArray<FileOrFolder>(contents));
	}

	
	private void accumulateDirectChildren(String candidate, String folder, List<FileOrFolder> result) {
		if (!candidate.startsWith(folder)) return;
		
		String name = Strings.removeStart(candidate, folder);
		if (name.indexOf('/') != -1) return; //Not a direct child.
		
		Hash hash = getHash(candidate);
		result.add(isFolder(candidate)
			? new FileOrFolder(name, hash)
			: new FileOrFolder(name, _lastModifiedDatesByPath.get(candidate), hash) 
		);
	}


	@Override
	public Hash remove(String path) {
		return movePath(path, null);
	}


	@Override
	public void rename(String from, String to) {
		movePath(from, to);
	}
	
	
	private Hash movePath(String from, String to) {
		boolean isFolder = isFolder(from);
		
		Hash result = replaceSinglePath(from, to);
		
		if (isFolder)
			replacePrefixes(from, to);
		
		return result;
	}

	
	private boolean isFolder(String path) {
		Long lastModified = _lastModifiedDatesByPath.get(path);
		if (lastModified == null) return false;
		return lastModified == -1;
	}

	
	private Hash replaceSinglePath(String from, String to) {
		Hash hash = _hashesByPath.remove(from);
		if (hash == null) throw new IllegalArgumentException("Path to be replaced is not mapped: " + from);
		_pathsByHash.remove(hash);
		long lastModified = _lastModifiedDatesByPath.remove(from);
		
		if (to != null)
			putPath(to, lastModified, hash);
		
		return hash;
	}
	

	private void replacePrefixes(String from, String to) {
		from += "/";
		if (to != null) to += "/";
		
		for (String candidate : allPaths())
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


	private String[] allPaths() {
		return _hashesByPath.keySet().toArray(new String[0]);
	}

}
