package dfcsantos.tracks.playlist.impl;

import java.io.File;

import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.playlist.Playlists;

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
