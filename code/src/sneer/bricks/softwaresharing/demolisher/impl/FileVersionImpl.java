package sneer.bricks.softwaresharing.demolisher.impl;

import sneer.bricks.softwaresharing.FileVersion;

class FileVersionImpl implements FileVersion {

	private final String _relativePath;
	private final byte[] _contents;
	private final byte[] _contentsInCurrentVersion;
	private final long _lastModified;
	private final Status _status;

	FileVersionImpl(String path, byte[] contents, byte[] contentsInCurrentVersion, long lastModified, boolean isCurrent) {
		_relativePath = path;
		_contents = contents;
		_contentsInCurrentVersion = contentsInCurrentVersion;
		_lastModified = lastModified;
		_status = isCurrent ? Status.CURRENT : Status.DIFFERENT;
	}

	@Override
	public byte[] contents() {
		return _contents;
	}

	@Override
	public byte[] contentsInCurrentVersion() {
		return _contentsInCurrentVersion;
	}

	@Override
	public String relativePath() {
		return _relativePath;
	}

	@Override
	public Status status() {
		return _status;
	}

	@Override
	public long lastModified() {
		return _lastModified;
	}

}
