package sneer.bricks.softwaresharing.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.List;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.softwaresharing.FileVersion;

class FileVersionImpl implements FileVersion {

	private final String _name;
	private final byte[] _contents;

	FileVersionImpl(List<String> path, byte[] contents) {
		_name = my(Lang.class).strings().join(path, "/");
		_contents = contents;
	}

	@Override
	public byte[] contents() {
		return _contents;
	}

	@Override
	public byte[] contentsInCurrentVersion() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public String name() {
		return _name;
	}

	@Override
	public Status status() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}
