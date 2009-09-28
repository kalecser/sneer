package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;
import dfcsantos.wusic.Track;

public class OwnTracks extends TrackSourceStrategy {

	static final TrackSourceStrategy INSTANCE = new OwnTracks();

	private final Light _noTracksFound = my(BlinkingLights.class).prepare(LightType.WARN);
	
	private RecursiveFolderPlaylist _playlist;
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
			_playlist.reload();
			if (!_playlist.hasMoreElements()) {
				my(BlinkingLights.class).turnOnIfNecessary(_noTracksFound, "No Tracks Found", "Please choose a folder with MP3 files in it or in its subfolders (Wusic > File > Configure Root Track Folder).");
				return null;
			}
		}
		my(BlinkingLights.class).turnOffIfNecessary(_noTracksFound);
		return _playlist.nextElement();
	}

	
	@Override
	void noWay(Track rejected) {
		if (!rejected.file().delete())
			my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to delete track", "Unable to delete track: " + rejected.file(), 7000);
	}

}
