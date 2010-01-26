package dfcsantos.tracks.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
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

	@Override
	public Sneer1024 calculateHashFor(Track track) {
		Sneer1024 hash = null;
		try {
			hash = my(Crypto.class).digest(track.file());
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error calculating track hash", "Error calculating hash for track: " + track.file(), e, 5000);
		}

		return hash;
	}

}
