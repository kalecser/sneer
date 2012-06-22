package sneer.bricks.expression.files.map.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sneer.bricks.hardware.cpu.crypto.Hash;


class FileMapData {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];


	static class Entry {
		final Hash hash;
		final long lastModified;
		final boolean isFolder;
		Entry(Hash hash_, long lastModified_, boolean isFolder_)
		{ hash = hash_; lastModified = lastModified_; isFolder = isFolder_; }
	}


	private final Map<Hash, Object>  _pathsByHash	= new HashMap<Hash, Object>();
	private final Map<String, Entry> _entriesByPath	= new HashMap<String, Entry>();


	synchronized
	void put(String path, long lastModified, Hash hash, boolean isFolder) {
		if (_entriesByPath.containsKey(path))
			remove(path);
		
		Object wrapping = _pathsByHash.get(hash);
		_pathsByHash.put(hash, addToWrapping(wrapping, path));
		_entriesByPath.put(path, new Entry(hash, lastModified, isFolder));
	}


	private Entry entry(String paths) {
		return _entriesByPath.get(paths);
	}


	synchronized
	Hash getHash(String path) {
		Entry entry = _entriesByPath.get(path);
		return entry == null ? null : entry.hash;
	}


	synchronized
	long getLastModified(String path) {
		return _entriesByPath.get(path).lastModified;
	}


	synchronized
	Entry remove(String path) {
		Entry result = _entriesByPath.remove(path);
		if (result == null) throw new IllegalArgumentException("Path to be removed is not mapped: " + path);
		
		Object wrapping = _pathsByHash.get(result.hash);
		Object newWrapping = removeFromWrapping(wrapping, path);
		if (newWrapping == null)
			_pathsByHash.remove(result.hash);
		else
			_pathsByHash.put(result.hash, newWrapping);
		
		return result;
	}


	synchronized
	String[] allPaths() {
		return _entriesByPath.keySet().toArray(EMPTY_STRING_ARRAY);
	}	
	
	private Object addToWrapping(Object previous, String path) {
		if (previous == null) return path;
		
		List<String> result;
		if (previous instanceof List<?>)
			result = (List<String>)previous;
		else {
			result = new ArrayList<String>();
			result.add((String)previous);
		}
		result.add(path);
		return result;
	}


	private Object removeFromWrapping(Object wrapping, String path) {
		if (wrapping instanceof String) return null;
		List<String> list = (List<String>) wrapping;
		list.remove(path);
		return list.size() == 1
			? list.get(0)
			: list;
	}

	synchronized
	List<String> getFolders(Hash hash) {
		return getPaths(hash, true);
	}
	
	synchronized
	List<String> getFiles(Hash hash) {
		return getPaths(hash, false);
	}
	
	private List<String> getPaths(Hash hash, boolean isFolder) {
		List<String> result = new ArrayList<String>();
		
		Object paths = _pathsByHash.get(hash);
		if (paths instanceof String) {
			String singlePath = (String) paths;
			if (entry(singlePath).isFolder == isFolder)
				result.add(singlePath);
		} else if (paths instanceof List)
			for (String path : (List<String>)paths)
				if (entry(path).isFolder == isFolder)
					result.add(path);
			
		return result;
	}	
}

