package dfcsantos.tracks.playlist.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.playlist.Playlist;

abstract class AbstractPlaylist implements Playlist {

	private final File _tracksFolder;
	private ListIterator<File> _trackIterator;

	private final Light _noTracksFound = my(BlinkingLights.class).prepare(LightType.WARN);

	AbstractPlaylist(File tracksFolder) {
		_tracksFolder = tracksFolder;
		initTrackIterator();
	}

	private void initTrackIterator() {
		_trackIterator = trackFiles().listIterator();	
	}

	private List<File> trackFiles() {
		List<File> tracks = my(Tracks.class).listMp3FilesFromFolder(_tracksFolder);
		setTracksOrder(tracks);

		return tracks;
	}

	abstract void setTracksOrder(List<File> tracks);

	@Override
	public Track nextTrack() {
		if (!_trackIterator.hasNext()) {
			rescan();
			if (!_trackIterator.hasNext()) {
				my(BlinkingLights.class).turnOnIfNecessary(_noTracksFound, "No Tracks Found", "Please choose a folder with MP3 files in it or in its subfolders (Wusic > File > Configure Root Track Folder).");
				return null;
			}
		}
		my(BlinkingLights.class).turnOffIfNecessary(_noTracksFound);

		final Track nextTrack = my(Tracks.class).newTrack(_trackIterator.next());
		_trackIterator.remove();

		return nextTrack;
	}

	private void rescan() {
		initTrackIterator();
	}

}
