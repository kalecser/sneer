package dfcsantos.tracks.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Sneer1024;
import dfcsantos.tracks.Track;

class TrackImpl implements Track {

	private final File _file;
	private final String _info;
	private Sneer1024 _hash;

	TrackImpl(File file) throws IOException {
		_file = file;
		_info = file.getName().replaceAll(".mp3", "");
		_hash = my(Crypto.class).digest(_file);
	}

	@Override
	public String name() {
		return _info; 
	}

	@Override
	public File file(){
		return _file;
	}

	@Override
	public Sneer1024 hash() {
		return _hash;
	}

	@Override
	public long lastModified() {
		return _file.lastModified();
	}

	@Override
	public int hashCode() {
		return _hash.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		TrackImpl other = (TrackImpl) obj;
		return _hash.equals(other._hash);
	}

	@Override
	public String toString() {
		return _file.getPath();
	}
}
