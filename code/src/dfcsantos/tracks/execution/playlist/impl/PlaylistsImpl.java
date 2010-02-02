package dfcsantos.tracks.execution.playlist.impl;

import java.io.File;

import dfcsantos.tracks.execution.playlist.Playlist;
import dfcsantos.tracks.execution.playlist.Playlists;

class PlaylistsImpl implements Playlists {

	@Override
	public Playlist newRandomPlaylist(File tracksFolder) {
		return new RandomPlaylist(tracksFolder);
	}

	@Override
	public Playlist newSequentialPlaylist(File tracksFolder) {
		return new SequentialPlaylist(tracksFolder);
	}

}
