package sneer.bricks.expression.files.map.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.cpu.crypto.Hash;


/**
 * 
 * IMPORTANT: Folders are represented with lastModifiedDate -1.
 *
 *   
 **/
class FileMapData {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];


	static class Entry {
		Entry(Hash hash_, long lastModified_) { hash = hash_; lastModified = lastModified_; }
		final Hash hash;
		final long lastModified;
	}


	static private final Map<Hash, String> _pathsByHash				= new ConcurrentHashMap<Hash, String>();
	static private final Map<String, Hash> _hashesByPath			= new ConcurrentHashMap<String, Hash>();
	static private final Map<String, Long> _lastModifiedDatesByPath	= new ConcurrentHashMap<String, Long>();


	static void put(String path, long lastModified, Hash hash) {
		_pathsByHash.put(hash, path);
		_hashesByPath.put(path, hash);
		_lastModifiedDatesByPath.put(path, lastModified);
	}


	static String getPath(Hash hash) {
		return _pathsByHash.get(hash);
	}


	static Hash getHash(String path) {
		return _hashesByPath.get(path);
	}


	static Long getLastModified(String path) {
		return _lastModifiedDatesByPath.get(path);
	}


	static Entry remove(String path) {
		Hash hash = _hashesByPath.remove(path);
		if (hash == null) throw new IllegalArgumentException("Path to be replaced is not mapped: " + path);
		_pathsByHash.remove(hash);
		long lastModified = _lastModifiedDatesByPath.remove(path);

		return new Entry(hash, lastModified);
	}


	static String[] allPaths() {
		return _hashesByPath.keySet().toArray(EMPTY_STRING_ARRAY);
	}


}
