package dfcsantos.tracks.execution.playlist;

import java.io.File;

import basis.brickness.Brick;


@Brick
public interface Playlists {

	Playlist newRandomPlaylist(File tracksFolder);

	Playlist newSequentialPlaylist(File tracksFolder);

}
