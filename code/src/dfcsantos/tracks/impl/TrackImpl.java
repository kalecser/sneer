package dfcsantos.tracks.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.crypto.Sneer1024;
import dfcsantos.tracks.Track;

class TrackImpl implements Track {

	private final File _file;
	private final String _info;
	private Sneer1024 _hash;

	TrackImpl(File file){
		_file = file;
		_info = file.getName().replaceAll(".mp3", "");
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
		if (_hash == null)
			_hash = my(FileMap.class).getHash(_file);
		return _hash;
	}

	@Override
	public long lastModified() {
		return _file.lastModified();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_hash == null) ? 0 : _hash.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		TrackImpl other = (TrackImpl) obj;
		if (_hash == null) {
			if (other._hash != null)
				return false;
		} else if (!_hash.equals(other._hash))
			return false;

		return true;
	}

}
