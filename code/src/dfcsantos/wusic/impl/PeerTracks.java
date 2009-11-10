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

class PeerTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new PeerTracks();

	private PeerTracks() {}

	@Override
	Playlist createPlaylist(File tracksFolder) {
		return my(Playlists.class).newRandomPlaylist(tracksFolder);
	}

	@Override
	File tracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();
	}

	void meToo(Track trackToKeep) {
		final File sharedTracksFolder = my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
		moveTrackToFolder(trackToKeep, sharedTracksFolder);
	}

	private void moveTrackToFolder(Track track, File destFolder) { // Move = Copy + Delete
		try {
			my(IO.class).files().copyFileToFolder(track.file(), destFolder);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Unable to copy track", "Unable to copy track: " + track.file(), 7000);
		}
		markForDisposal(track);
	}

}
