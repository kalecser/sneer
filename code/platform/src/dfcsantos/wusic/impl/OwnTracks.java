package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Enumeration;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;
import dfcsantos.wusic.Track;

public class OwnTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new OwnTracks();
	
	private Enumeration<Track> _playlist;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGC;
	
	
	{	
		_refToAvoidGC = my(OwnTracksFolderKeeper.class).ownTracksFolder().addReceiver(new Consumer<File>() {@Override public void consume(File ownTracksFolder) {
			_playlist = new RecursiveFolderPlaylist(ownTracksFolder);
		}});
	}
	
	
	private OwnTracks() {}
	
	
	@Override
	Track nextTrack()  {
		if (!_playlist.hasMoreElements()) {
			my(BlinkingLights.class).turnOn(LightType.WARN, "No songs found", "Please choose a folder with MP3 files in it or in its subfolders.", 10000);
			return null;
		}
		return _playlist.nextElement();
	}


	@Override
	void noWay() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}


}
