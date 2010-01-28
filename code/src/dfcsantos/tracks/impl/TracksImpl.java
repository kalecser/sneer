package dfcsantos.tracks.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.io.IO;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;

class TracksImpl implements Tracks {

	@Override
	public List<File> listMp3FilesFromFolder(File folder) {
		return new ArrayList<File>(my(IO.class).files().listFiles(folder, new String[] { "mp3","MP3" }, true));
	}

	@Override
	public List<Track> listTracksFromFolder(File folder) {
		List<Track> tracks = new ArrayList<Track>();
		for (File trackFile : listMp3FilesFromFolder(folder)) {
			tracks.add(newTrack(trackFile));
		}
		return tracks;
	}

	@Override
	public Track newTrack(File trackFile) {
		return new TrackImpl(trackFile);
	}

}
