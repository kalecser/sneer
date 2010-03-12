package dfcsantos.tracks.execution.playlist.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sneer.bricks.hardware.clock.Clock;

class RandomPlaylist extends AbstractPlaylist {

	RandomPlaylist(File tracksFolder) {
		super(tracksFolder);
	}

	@Override
	public void sortTracks(List<File> tracks) {
		super.sortTracks(tracks); // It sort alphabetically first to make it deterministic and therefore testable 
		Collections.shuffle(tracks, new Random(my(Clock.class).time().currentValue()));
	}

}
