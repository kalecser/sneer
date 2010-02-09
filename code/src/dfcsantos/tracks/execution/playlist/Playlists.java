package dfcsantos.tracks.execution.playlist;

import java.io.File;

import sneer.foundation.brickness.Brick;

@Brick
public interface Playlists {

	Playlist newRandomPlaylist(File tracksFolder);

	Playlist newSequentialPlaylist(File tracksFolder);

}
