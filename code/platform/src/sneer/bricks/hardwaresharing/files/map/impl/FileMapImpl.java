package sneer.bricks.hardwaresharing.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.crypto.Sneer1024;

class FileMapImpl implements FileMap {

	private final Map<Sneer1024, File> _map = new HashMap<Sneer1024, File>();

	
	@Override
	public Sneer1024 put(File file) throws IOException {
		Sneer1024 hash = my(Hasher.class).hash(file);
		_map.put(hash, file);
		return hash;
	}

	
	@Override
	public File get(Sneer1024 hash) {
		return _map.get(hash); 
	}

}
