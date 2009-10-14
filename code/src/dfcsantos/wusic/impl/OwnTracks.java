package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.playlist.Playlists;


public class OwnTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new OwnTracks();
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGC;

	private OwnTracks() {	
		_refToAvoidGC = my(TracksFolderKeeper.class).ownTracksFolder().addReceiver(new Consumer<File>() {@Override public void consume(File ownTracksFolder) {
			setTracksFolder(ownTracksFolder);
			initPlaylist();
		}});
	}

	@Override
	Playlist createPlaylist(File tracksFolder) {
		return isShuffle() ? my(Playlists.class).newRandomPlaylist(tracksFolder) : my(Playlists.class).newSequentialPlaylist(tracksFolder);
	}

}
