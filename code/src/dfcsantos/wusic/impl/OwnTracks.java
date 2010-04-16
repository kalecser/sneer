package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.execution.playlist.Playlist;
import dfcsantos.tracks.execution.playlist.Playlists;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;


public class OwnTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new OwnTracks();
	
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGC;

	private boolean _isShuffle;

	private final Light _noTracksFound = my(BlinkingLights.class).prepare(LightType.WARNING);


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

	@Override
	Track nextTrack() {
		Track result = super.nextTrack();
		
		if (result == null)
			my(BlinkingLights.class).turnOnIfNecessary(_noTracksFound, "No Tracks to Play", "Please choose a folder with MP3 files in 'Wusic > Own Tracks > Playing Folder'.");
		else
			my(BlinkingLights.class).turnOffIfNecessary(_noTracksFound);
		
		return result;
	}

}
