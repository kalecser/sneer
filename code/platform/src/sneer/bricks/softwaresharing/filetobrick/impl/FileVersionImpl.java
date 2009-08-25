package sneer.bricks.softwaresharing.filetobrick.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.List;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.softwaresharing.FileVersion;

class FileVersionImpl implements FileVersion {

	private final String _name;
	private final byte[] _contents;
	private final Status _status;

	FileVersionImpl(List<String> path, byte[] contents, boolean isCurrent) {
		_name = my(Lang.class).strings().join(path, "/");
		_contents = contents;
		_status = isCurrent ? Status.CURRENT : Status.DIFFERENT;
	}

	@Override
	public byte[] contents() {
		return _contents;
	}

	@Override
	public byte[] contentsInCurrentVersion() {
		return _status == Status.CURRENT ? _contents : null;
	}

	@Override
	public String name() {
		return _name;
	}

	@Override
	public Status status() {
		return _status;
	}

}
