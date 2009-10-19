package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.playlist.Playlists;

class SharedTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new SharedTracks();

	// private final ListRegister<Track> _tracksToDispose = my(CollectionSignals.class).newListRegister();

	private SharedTracks() {
		setTracksFolder(my(TracksFolderKeeper.class).candidateTracksFolder());
		initPlaylist();
	};

	@Override
	Playlist createPlaylist(File tracksFolder) {
		return my(Playlists.class).newRandomPlaylist(tracksFolder);
	}

	void meToo(Track trackToKeep) {
		final File sharedTracksFolder = my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
		moveTrackToFolder(trackToKeep, sharedTracksFolder);
	}

	private void moveTrackToFolder(Track track, File destFolder) { // Move = Copy + Delete
		try {
			my(IO.class).files().copyFileToFolder(track.file(), destFolder);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to copy track", "Unable to copy track: " + track.file(), 7000);
		}
		disposeTrack(track);
	}

	private void disposeTrack(Track track) {
		track.dispose(); // Mark track for late disposal since it might still be playing
		// _tracksToDispose.add(track);
	}

	void noWay(Track trackToDispose) {
		disposeTrack(trackToDispose);		
	}

}
