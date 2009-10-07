package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;
import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.playlist.Playlists;

abstract class TrackSourceStrategy {
	
	private Playlist _playlist;
	private File _tracksFolder;
	private boolean _isShuffleMode;

	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGC;

	{	
		_refToAvoidGC = my(OwnTracksFolderKeeper.class).ownTracksFolder().addReceiver(new Consumer<File>() {@Override public void consume(File ownTracksFolder) {
			setTracksFolder(ownTracksFolder);
			initPlaylist();
		}});
	}

	abstract String tracksSubfolder();

	private void setTracksFolder(File tracksFolder) {
		_tracksFolder = tracksFolder; 
	}

	private Playlist createPlaylist(File tracksFolder) {
		return isShuffleMode() ? my(Playlists.class).newRandomPlaylist(tracksFolder) : my(Playlists.class).newSequentialPlaylist(tracksFolder);
	}

	private void initPlaylist() {
		_playlist = createPlaylist(new File(_tracksFolder, tracksSubfolder()));
	}

	protected void setShuffleMode(boolean isShuffleMode) {
		_isShuffleMode = isShuffleMode;
		initPlaylist();
	}

	protected boolean isShuffleMode() {
		return _isShuffleMode;
	}

	protected Track nextTrack() {
		return _playlist.nextTrack();
	}

	protected void noWay(Track rejected) {
		if (!rejected.file().delete())
			my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to delete track", "Unable to delete track: " + rejected.file(), 7000);
	}

}
