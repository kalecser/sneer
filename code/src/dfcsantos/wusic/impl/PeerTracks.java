package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.playlist.Playlists;

class PeerTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new PeerTracks();

	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGC;

	private PeerTracks() {
		_refToAvoidGC = my(TracksFolderKeeper.class).peerTracksFolder().addReceiver(new Consumer<File>() {@Override public void consume(File peerTracksFolder) {
			setTracksFolder(peerTracksFolder);
			initPlaylist();
		}});
	};

	@Override
	Playlist createPlaylist(File tracksFolder) {
		return my(Playlists.class).newRandomPlaylist(tracksFolder);
	}

	void meToo() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	void noWay(Track rejected) {
		if (!rejected.file().delete())
			my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to delete track", "Unable to delete track: " + rejected.file(), 7000);
	}

}
