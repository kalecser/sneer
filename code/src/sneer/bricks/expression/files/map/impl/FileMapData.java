package sneer.bricks.expression.files.map.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.cpu.crypto.Hash;


class FileMapData {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];


	static class Entry {
		Entry(Hash hash_, long lastModified_) { hash = hash_; lastModified = lastModified_; }
		final Hash hash;
		final long lastModified;
	}


	private final Map<Hash, String>  _pathsByHash	= new ConcurrentHashMap<Hash, String>();
	private final Map<String, Entry> _entriesByPath	= new ConcurrentHashMap<String, Entry>();


	void put(String path, long lastModified, Hash hash) {
		_pathsByHash.put(hash, path);
		_entriesByPath.put(path, new Entry(hash, lastModified));
	}


	String getPath(Hash hash) {
		return _pathsByHash.get(hash);
	}


	Hash getHash(String path) {
		Entry entry = _entriesByPath.get(path);
		return entry == null ? null : entry.hash;
	}


	Long getLastModified(String path) {
		Entry entry = _entriesByPath.get(path);
		return entry == null ? null : entry.lastModified;
	}


	Entry remove(String path) {
		Entry result = _entriesByPath.remove(path);
		if (result == null) throw new IllegalArgumentException("Path to be removed is not mapped: " + path);
		_pathsByHash.remove(result.hash);
		return result;
	}


	String[] allPaths() {
		return _entriesByPath.keySet().toArray(EMPTY_STRING_ARRAY);
	}


}
