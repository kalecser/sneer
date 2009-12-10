package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;
import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.playlist.Playlists;


public class OwnTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new OwnTracks();
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGC;

	private boolean _isShuffle;

	private OwnTracks() {	
		_refToAvoidGC = my(TracksFolderKeeper.class).playingFolder().addReceiver(new Consumer<File>() {@Override public void consume(File ownTracksFolder) {
			initPlaylist(ownTracksFolder);
		}});
	}

	@Override
	Playlist createPlaylist(File tracksFolder) {
		return _isShuffle ? my(Playlists.class).newRandomPlaylist(tracksFolder) : my(Playlists.class).newSequentialPlaylist(tracksFolder);
	}

	@Override
	File tracksFolder() {
		return my(TracksFolderKeeper.class).playingFolder().currentValue();
	}

	void setShuffle(boolean shuffle) {
		_isShuffle = shuffle;
		initPlaylist();
	}

}
