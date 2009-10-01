package sneer.bricks.hardwaresharing.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.crypto.Sneer1024;

class FileMapImpl implements FileMap {

	@Override
	public Sneer1024 put(File file) throws IOException {
		return my(Hasher.class).hash(file);
	}

}
