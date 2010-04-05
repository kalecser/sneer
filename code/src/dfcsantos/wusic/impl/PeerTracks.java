package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.execution.playlist.Playlist;
import dfcsantos.tracks.execution.playlist.Playlists;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

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
		//Implement Create event to notify listeners (like the MusicalTasteMatcher) of track approval/rejection.
		moveTrackToFolder(trackToKeep, sharedTracksFolder());
	}

	@Override
	void deleteTrack(Track rejected) {
		decrementDownloadCounter();
		super.deleteTrack(rejected);
	}

	private void moveTrackToFolder(Track track, File destFolder) { // Move = Copy + Delete
		my(Logger.class).log("Moving track {} to shared folder", track.file());
		try {
			my(IO.class).files().copyFileToFolder(track.file(), destFolder);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Unable to copy track", "Unable to copy track: " + track.file(), 7000);
		}
		decrementDownloadCounter();
		markForDisposal(track);
		updateFileMap(track.file());
	}

	private void updateFileMap(File tmpTrack) {
		Sneer1024 hash = my(FileMap.class).remove(tmpTrack);
		File keptTrack = new File(sharedTracksFolder(), tmpTrack.getName());
		my(FileMap.class).putFile(keptTrack, hash);
	}

	private File sharedTracksFolder() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
	}

	private void decrementDownloadCounter() {
		my(TrackDownloadCounter.class).decrement();
	}

}
