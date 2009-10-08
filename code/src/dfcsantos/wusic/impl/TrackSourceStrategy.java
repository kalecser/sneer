package dfcsantos.wusic.impl;

import java.io.File;

import dfcsantos.tracks.Track;
import dfcsantos.tracks.playlist.Playlist;

abstract class TrackSourceStrategy {

	private Playlist _playlist;
	private File _tracksFolder;
	private boolean _isShuffleMode;

	protected void setTracksFolder(File tracksFolder) {
		_tracksFolder = tracksFolder; 
	}

	void setShuffleMode(boolean isShuffleMode) {
		_isShuffleMode = isShuffleMode;
		initPlaylist();
	}

	protected boolean isShuffleMode() {
		return _isShuffleMode;
	}

	protected void initPlaylist() {
		_playlist = createPlaylist(_tracksFolder);
	}

	abstract Playlist createPlaylist(File tracksFolder);

	protected Track nextTrack() {
		return _playlist.nextTrack();
	}

}
