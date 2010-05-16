package sneer.bricks.expression.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.impl.FileMapData.Entry;
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

	private final FileMapData _data = new FileMapData();

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
		_data.put(path, lastModified, hash);
	}
	
	
	@Override
	public String getFile(Hash hash) {
		String path = _data.getPath(hash);
		if (path == null) return null;
		return isFolder(path)
			? null
			: path;
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
		String path = _data.getPath(hash);
		if (path == null) return null;
		if (!isFolder(path)) return null;

		String folder = path + "/";
		
		List<FileOrFolder> contents = new ArrayList<FileOrFolder>();
		for (String candidate : _data.allPaths())
			accumulateDirectChildren(candidate, folder, contents);
		
		Collections.sort(contents, new Comparator<FileOrFolder>() { @Override public int compare(FileOrFolder f1, FileOrFolder f2) {
			return f1.name.compareTo(f2.name);
		}});
		
		return new FolderContents(new ImmutableArray<FileOrFolder>(contents));
	}

	
	private void accumulateDirectChildren(String candidate, String folder, List<FileOrFolder> result) {
		if (!candidate.startsWith(folder)) return;
		
		String name = Strings.removeStart(candidate, folder);
		if (name.indexOf('/') != -1) return; //Not a direct child.
		
		Hash hash = getHash(candidate);
		result.add(isFolder(candidate)
			? new FileOrFolder(name, hash)
			: new FileOrFolder(name, getLastModified(candidate), hash) 
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
		Long lastModified = _data.getLastModified(path);
		if (lastModified == null) return false;
		return lastModified == -1;
	}

	
	private Hash replaceSinglePath(String from, String to) {
		Entry entry = _data.remove(from);
		
		if (to != null)
			putPath(to, entry.lastModified, entry.hash);
		
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

}
