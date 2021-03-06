package dfcsantos.music.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.exchange.downloads.counter.TrackDownloadCounter;
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
		return my(TracksFolderKeeper.class).inboxFolder();
	}

	void meToo(Track trackToKeep) {
		if (isMarkedForDisposal(trackToKeep)) return;
		moveTrackToFolder(trackToKeep, noveltiesFolder());
	}

	
	private void moveTrackToFolder(Track track, File destFolder) { // Move = Copy + Delete
		my(Logger.class).log("Moving track {} to shared folder", track.file());
		try {
			my(IO.class).files().copyFileToFolder(track.file(), destFolder);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Unable to copy track", "Unable to copy track: " + track.file(), 7000);
		}
		condemn(track);
		updateFileMap(track.file());
	}

	
	@Override
	protected void onTrackKilled(Track victim) {
		super.onTrackKilled(victim);
		my(TrackDownloadCounter.class).refresh();
	}
	
	
	private void updateFileMap(File tmpTrack) {
		Hash hash = my(FileMap.class).remove(tmpTrack.getAbsolutePath());
		File keptTrack = new File(noveltiesFolder(), tmpTrack.getName());
		my(FileMap.class).putFile(keptTrack.getAbsolutePath(), keptTrack.length(), keptTrack.lastModified(), hash);
	}

	private File noveltiesFolder() {
		return my(TracksFolderKeeper.class).noveltiesFolder();
	}

}
