package dfcsantos.tracks.impl;

import java.io.File;

import dfcsantos.tracks.Track;

class TrackImpl implements Track {

	private final File _file;
	private final String _info;

	TrackImpl(File file) {
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
	public int hashCode() {
		return _file.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		TrackImpl other = (TrackImpl) obj;
		return _file.equals(other._file);
	}

	@Override
	public String toString() {
		return _file.getPath();
	}

}
