package dfcsantos.wusic.impl;

import java.io.File;

import dfcsantos.tracks.Track;
import dfcsantos.tracks.playlist.Playlist;

abstract class TrackSourceStrategy {

	private Playlist _playlist;
	private File _tracksFolder;
	private boolean _isShuffle;

	protected void setTracksFolder(File tracksFolder) {
		_tracksFolder = tracksFolder; 
	}

	void setShuffle(boolean shuffle) {
		_isShuffle = shuffle;
		initPlaylist();
	}

	protected boolean isShuffle() {
		return _isShuffle;
	}

	protected void initPlaylist() {
		_playlist = createPlaylist(_tracksFolder);
	}

	abstract Playlist createPlaylist(File tracksFolder);

	protected Track previousTrack() {
		return _playlist.previousTrack();
	}

	protected Track nextTrack() {
		return _playlist.nextTrack();
	}

}
