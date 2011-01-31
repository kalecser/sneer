package sneer.bricks.expression.files.map.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.IO.Filenames;

class FileMapImpl implements FileMap {

	private static final Filenames Filenames = my(IO.class).filenames();
	
	
	private final FileMap _delegate = new NormalizedFileMap();
	
	
	@Override
	public FolderContents getFolderContents(Hash hash) {
		return _delegate.getFolderContents(hash);
	}

	@Override
	public Hash getHash(String path) {
		return _delegate.getHash(normalize(path));
	}

	@Override
	public long getLastModified(String file) {
		return _delegate.getLastModified(normalize(file));
	}

	@Override
	public String getFile(Hash hash) {
		return _delegate.getFile(hash);
	}

	@Override
	public String getFolder(Hash hash) {
		return _delegate.getFolder(hash);
	}

	@Override
	public void putFile(String path, long lastModified, Hash hash) {
		_delegate.putFile(normalize(path), lastModified, hash);
	}

	@Override
	public void putFolder(String path, Hash hash) {
		_delegate.putFolder(normalize(path), hash);
	}

	@Override
	public Hash remove(String path) {
		return _delegate.remove(normalize(path));
	}

	@Override
	public void rename(String fromPath, String toPath) {
		_delegate.rename(normalize(fromPath), normalize(toPath));
	}

	private String normalize(String path) {
		String result = Filenames.separatorsToUnix(path);
		if (result.endsWith("/")) throw new IllegalArgumentException("Path should not have trailing slash: " + path);
		return result;
	}

}
